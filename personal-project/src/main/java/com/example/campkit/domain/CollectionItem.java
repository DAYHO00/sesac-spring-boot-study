package com.example.campkit.domain;

public class CollectionItem {

    private final Long id;
    private final String name;
    private final int price;

    public CollectionItem(Long id, String name, int price) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("장비 이름은 비어 있을 수 없습니다.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }

        this.id = id;
        this.name = name.trim();
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}