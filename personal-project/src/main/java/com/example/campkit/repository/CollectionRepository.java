
package com.example.campkit.repository;

import com.example.campkit.domain.CollectionItem;
import java.util.List;
import java.util.Optional;

public interface CollectionRepository {

    // 캠핑 장비 저장
    CollectionItem save(CollectionItem item);

    // 전체 캠핑 장비 조회
    List<CollectionItem> findAll();

    // ID로 특정 장비 조회
    Optional<CollectionItem> findById(Long id);
}
