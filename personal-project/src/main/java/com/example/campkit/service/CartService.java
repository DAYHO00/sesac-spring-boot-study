
package com.example.campkit.service;

import com.example.campkit.domain.*;
import com.example.campkit.dto.CartPreview;
import com.example.campkit.exception.*;
import com.example.campkit.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cart;
    private final CollectionService collection;

    public CartService(CartRepository cart, CollectionService collection) {
        this.cart = cart;
        this.collection = collection;
    }

    // 장바구니 추가
    public synchronized CartItem add(long itemId, int quantity) {
        validateQuantity(quantity);

        var existing = cart.findByCollectionItemId(itemId);

        if (existing.isPresent()) {
            CartItem old = existing.get();

            int combined = old.getQuantity() + quantity;

            validateQuantity(combined);
            validateStock(old.getStock(), combined);

            CartItem updated = old.withQuantity(combined);
            cart.replace(updated);
            return updated;
        }

        // 상품 존재 확인
        CollectionItem source = collection.findById(itemId);

        // 최대 20종
        if (cart.findAll().size() >= 20) {
            throw new ConflictException("장바구니는 최대 20종까지 가능합니다.");
        }

        validateStock(source.getStock(), quantity);

        CartItem item = new CartItem(
                0,
                source.getId(),
                source.getName(),
                source.getPrice(),
                source.getStock(),
                quantity
        );

        return cart.saveNew(item);
    }

    // 전체 조회
    public List<CartItem> findAll() {
        return cart.findAll();
    }

    // 수량 변경
    public synchronized CartItem update(long cartId, int quantity) {
        validateQuantity(quantity);

        CartItem old = cart.findById(cartId)
                .orElseThrow(() ->
                        new NotFoundException("장바구니 항목을 찾을 수 없습니다."));

        validateStock(old.getStock(), quantity);

        CartItem updated = old.withQuantity(quantity);
        cart.replace(updated);

        return updated;
    }

    // 삭제
    public synchronized void delete(long cartId) {
        if (cart.findById(cartId).isEmpty()) {
            throw new NotFoundException("장바구니 항목을 찾을 수 없습니다.");
        }

        cart.delete(cartId);
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 5) {
            throw new ConflictException("수량은 1~5개여야 합니다.");
        }
    }

    private void validateStock(Integer stock, int quantity) {
        if (stock != null && quantity > stock) {
            throw new ConflictException("상품 재고가 부족합니다.");
        }
    }

    // 쿠폰 및 배송비 미리보기
    public CartPreview preview(String couponCode) {

        Coupon coupon;

        try {
            coupon = Coupon.valueOf(
                    couponCode == null ? "NONE" : couponCode
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 쿠폰입니다.");
        }

        List<CartItem> items = cart.findAll();

        long subtotal = 0;

        try {
            for (CartItem item : items) {
                subtotal = Math.addExact(subtotal, item.getLineAmount());
            }
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("계산 가능한 금액 범위를 초과했습니다.");
        }

        if (items.isEmpty()) {
            if (coupon != Coupon.NONE) {
                throw new IllegalArgumentException(
                        "빈 장바구니에는 쿠폰을 사용할 수 없습니다."
                );
            }
            return new CartPreview(0, 0, 0, 0, 0, coupon);
        }

        long discount = switch (coupon) {

            case NONE -> 0;

            case PERCENT10 -> Math.min(subtotal / 10, 20_000L);

            case FIXED15000 -> {
                if (subtotal < 100_000) {
                    throw new IllegalArgumentException(
                            "상품 합계 100,000원 이상에서 사용할 수 있습니다."
                    );
                }
                yield 15_000L;
            }
        };

        long discountedSubtotal = subtotal - discount;

        long shipping = discountedSubtotal >= 100_000 ? 0 : 5_000;

        long finalAmount = Math.addExact(discountedSubtotal, shipping);

        return new CartPreview(
                subtotal,
                discount,
                discountedSubtotal,
                shipping,
                finalAmount,
                coupon
        );
    }
}
