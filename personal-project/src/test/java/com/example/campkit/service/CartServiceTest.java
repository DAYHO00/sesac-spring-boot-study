
package com.example.campkit.service;

import com.example.campkit.domain.*;
import com.example.campkit.exception.*;
import com.example.campkit.external.*;

import com.example.campkit.repository.original.MemoryCartRepository;
import com.example.campkit.repository.original.MemoryCollectionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    // 외부 API Mock 객체
    ExternalProductClient client = mock(ExternalProductClient.class);

    // 위시리스트 서비스
    CollectionService collections = new CollectionService(
            new MemoryCollectionRepository(),
            new ExternalProductService(client)
    );

    // 장바구니 서비스
    CartService cart = new CartService(
            new MemoryCartRepository(),
            collections
    );

    @Test
    void 동일상품_수량합산() {
        long id = collections.register("랜턴", 20000).getId();

        cart.add(id, 2);
        cart.add(id, 1);

        // 동일 상품은 한 항목으로 관리하며 수량 합산
        assertEquals(1, cart.findAll().size());
        assertEquals(3, cart.findAll().get(0).getQuantity());
    }

    @Test
    void 최대수량_초과검증() {
        long id = collections.register("의자", 10000).getId();

        cart.add(id, 3);

        // 합산 수량 6개는 최대 수량 5개 초과
        assertThrows(
                ConflictException.class,
                () -> cart.add(id, 3)
        );

        // 실패 이후 기존 수량 유지
        assertEquals(3, cart.findAll().get(0).getQuantity());
    }

    @Test
    void 품절상품_등록방지() {

        // 재고가 0인 외부 상품 응답 설정
        when(client.findById(7)).thenReturn(
                new DummyJsonProduct(
                        7L,
                        "텐트",
                        new BigDecimal("10"),
                        "camp",
                        null,
                        0
                )
        );

        CollectionItem item = collections.importExternal(7);

        // 품절 상품 장바구니 추가 방지
        assertThrows(
                ConflictException.class,
                () -> cart.add(item.getId(), 1)
        );

        // 실패 시 장바구니 데이터 유지
        assertTrue(cart.findAll().isEmpty());
    }

    @Test
    void 쿠폰할인_배송비계산() {
        long id = collections.register("텐트", 110000).getId();

        cart.add(id, 1);

        // 쿠폰 없음: 110,000원
        assertEquals(
                110000,
                cart.preview("NONE").finalAmount()
        );

        // 10% 할인 + 배송비: 104,000원
        assertEquals(
                104000,
                cart.preview("PERCENT10").finalAmount()
        );

        // 15,000원 할인 + 배송비: 100,000원
        assertEquals(
                100000,
                cart.preview("FIXED15000").finalAmount()
        );
    }

    @Test
    void 최소주문금액_검증() {
        long id = collections.register("랜턴", 50000).getId();

        cart.add(id, 1);

        // 10만원 미만은 고정 할인 쿠폰 사용 불가
        assertThrows(
                IllegalArgumentException.class,
                () -> cart.preview("FIXED15000")
        );
    }

    @Test
    void 최대할인금액_검증() {
        long id = collections.register("텐트", 250000).getId();

        cart.add(id, 1);

        // 10%는 25,000원이지만 최대 20,000원 할인
        assertEquals(
                20000,
                cart.preview("PERCENT10").discount()
        );
    }

    @Test
    void 장바구니_상품삭제() {
        long id = collections.register("의자", 20000).getId();

        CartItem item = cart.add(id, 1);

        cart.delete(item.getId());

        // 삭제 후 장바구니가 비어 있는지 확인
        assertTrue(cart.findAll().isEmpty());
    }
}
