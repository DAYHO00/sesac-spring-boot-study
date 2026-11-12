
package com.example.campkit.repository.original;

import com.example.campkit.domain.CollectionItem;
import com.example.campkit.domain.SourceType;
import com.example.campkit.repository.CollectionRepository;

import java.util.*;

//@Repository
public class MemoryCollectionRepository implements CollectionRepository {

    private final Map<Long, CollectionItem> items = new LinkedHashMap<>();
    private long nextId = 1L;

    @Override
    public synchronized CollectionItem save(CollectionItem item) {
        CollectionItem saved = item.withId(nextId++);
        items.put(saved.getId(), saved);
        return saved;
    }

    @Override
    public synchronized List<CollectionItem> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public synchronized Optional<CollectionItem> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public synchronized Optional<CollectionItem> findByExternalId(Long externalId) {
        return items.values().stream()
                .filter(item -> item.getSourceType() == SourceType.DUMMYJSON)
                .filter(item -> Objects.equals(item.getExternalId(), externalId))
                .findFirst();
    }
}
