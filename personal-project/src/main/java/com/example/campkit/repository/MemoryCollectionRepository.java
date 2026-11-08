
package com.example.campkit.repository;

import com.example.campkit.domain.CollectionItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemoryCollectionRepository implements CollectionRepository {

    private final List<CollectionItem> items = new ArrayList<>();
    private long nextId = 1L;

    @Override
    public CollectionItem save(CollectionItem item) {

        CollectionItem savedItem = new CollectionItem(nextId++, item.getName(), item.getPrice());
        items.add(savedItem);
        return savedItem;
    }

    @Override
    public List<CollectionItem> findAll() {
        return new ArrayList<>(items);
    }

    @Override
    public Optional<CollectionItem> findById(Long id) {
        return items.stream().filter(item -> item.getId().equals(id)).findFirst();
    }
}
