
package com.example.campkit.controller;

import com.example.campkit.domain.CollectionItem;
import com.example.campkit.service.CollectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    // 1. 캠핑 장비 등록
    @PostMapping
    public CollectionItem register(
            @RequestParam String name,
            @RequestParam int price) {

        return collectionService.register(name, price);
    }

    // 2. 전체 캠핑 장비 조회
    @GetMapping
    public List<CollectionItem> findAll() {
        return collectionService.findAll();
    }

    // 3. ID로 단건 조회
    @GetMapping("/{id}")
    public CollectionItem findById(@PathVariable Long id) {
        return collectionService.findById(id);
    }

    // 4. 예산별 캠핑 장비 추천
    @GetMapping("/recommend")
    public List<CollectionItem> recommend(@RequestParam int budget) {
        return collectionService.recommend(budget);
    }
}
