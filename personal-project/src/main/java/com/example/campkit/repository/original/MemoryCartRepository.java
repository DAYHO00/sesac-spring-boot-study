
package com.example.campkit.repository.original;

import com.example.campkit.domain.CartItem;
import com.example.campkit.repository.CartRepository;

import java.util.*;

//@Repository
public class MemoryCartRepository implements CartRepository {

    private final Map<Long, CartItem> items = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public synchronized List<CartItem> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public synchronized Optional<CartItem> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public synchronized Optional<CartItem> findByCollectionItemId(long id) {
        return items.values().stream()
                .filter(item -> item.getCollectionItemId() == id)
                .findFirst();
    }

    @Override
    public synchronized CartItem saveNew(CartItem item) {
        CartItem saved = item.withId(nextId++);
        items.put(saved.getId(), saved);
        return saved;
    }

    @Override
    public synchronized void replace(CartItem item) {
        items.put(item.getId(), item);
    }

    @Override
    public synchronized void delete(long id) {
        items.remove(id);
    }
}
