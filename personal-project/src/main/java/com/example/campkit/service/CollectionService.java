
package com.example.campkit.service;

import com.example.campkit.domain.CollectionItem;
import com.example.campkit.repository.CollectionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CollectionService {

    private final CollectionRepository repository;

    public CollectionService(CollectionRepository repository) {
        this.repository = repository;
    }

    // 1. 캠핑 장비 등록
    public CollectionItem register(String name, int price) {
        CollectionItem item = new CollectionItem(null, name, price);
        return repository.save(item);
    }

    // 2. 전체 장비 조회
    public List<CollectionItem> findAll() {
        return repository.findAll();
    }

    // 3. ID로 단건 조회
    public CollectionItem findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 장비를 찾을 수 없습니다."));
    }

    // 4. 예산별 장비 추천
    public List<CollectionItem> recommend(int budget) {

        if (budget < 0) {
            throw new IllegalArgumentException("예산은 0원 이상이어야 합니다.");
        }

        return repository.findAll().stream()
                .filter(item -> item.getPrice() <= budget) // 개별 가격이 예산 이하인 장비만 선택
                .sorted(Comparator.comparingInt(CollectionItem::getPrice)) // 가격 오름차순 정렬
                .limit(3) // 최대 3개 선택
                .toList();
    }
}
