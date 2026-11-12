
package com.example.campkit.repository;

import com.example.campkit.domain.CollectionItem;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository {

    CollectionItem save(CollectionItem item);

    List<CollectionItem> findAll();

    Optional<CollectionItem> findById(Long id);

    Optional<CollectionItem> findByExternalId(Long externalId);
}
