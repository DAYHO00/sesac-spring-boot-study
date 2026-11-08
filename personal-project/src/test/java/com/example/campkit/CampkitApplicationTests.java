
package com.example.campkit;

import com.example.campkit.domain.CollectionItem;
import com.example.campkit.repository.MemoryCollectionRepository;
import com.example.campkit.service.CollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionServiceTest {

	private CollectionService service;

	@BeforeEach
	void setUp() {
		service = new CollectionService(new MemoryCollectionRepository());
	}

	@Test
	void 장비_등록과_조회() {
		CollectionItem item = service.register(" 랜턴 ", 15000);

		assertEquals(1L, item.getId());
		assertEquals("랜턴", item.getName());
		assertEquals(15000, item.getPrice());
		assertEquals(1, service.findAll().size());
		assertEquals("랜턴", service.findById(item.getId()).getName());
	}

	@Test
	void 잘못된_이름과_가격_거부() {
		assertThrows(IllegalArgumentException.class,
				() -> service.register("  ", 1000));

		assertThrows(IllegalArgumentException.class,
				() -> service.register(null, 1000));

		assertThrows(IllegalArgumentException.class,
				() -> service.register("텐트", -1000));

		assertTrue(service.findAll().isEmpty());
	}

	@Test
	void 예산에_맞는_장비_추천() {
		service.register("텐트", 120000);
		service.register("랜턴", 15000);
		service.register("의자", 30000);
		service.register("버너", 45000);
		service.register("카라비너", 5000);

		List<CollectionItem> result = service.recommend(50000);

		assertEquals(3, result.size());
		assertEquals("카라비너", result.get(0).getName());
		assertEquals("랜턴", result.get(1).getName());
		assertEquals("의자", result.get(2).getName());
	}

	@Test
	void 무료_장비와_음수_예산() {
		service.register("무료 샘플", 0);

		assertEquals(1, service.recommend(0).size());

		assertThrows(IllegalArgumentException.class,
				() -> service.recommend(-1));

		assertTrue(service.recommend(100).size() == 1);
	}

	@Test
	void 추천_후에도_원본_순서_유지() {
		service.register("텐트", 100000);
		service.register("랜턴", 10000);
		service.register("의자", 30000);

		service.recommend(100000);

		List<CollectionItem> items = service.findAll();

		assertEquals("텐트", items.get(0).getName());
		assertEquals("랜턴", items.get(1).getName());
		assertEquals("의자", items.get(2).getName());
	}

	@Test
	void 존재하지_않는_ID_조회() {
		assertThrows(IllegalArgumentException.class,
				() -> service.findById(999L));
	}

	@Test
	void 같은_가격이면_등록_순서_유지() {
		service.register("랜턴 A", 10000);
		service.register("랜턴 B", 10000);
		service.register("랜턴 C", 10000);
		service.register("랜턴 D", 10000);

		List<CollectionItem> result = service.recommend(10000);

		assertEquals(3, result.size());
		assertEquals("랜턴 A", result.get(0).getName());
		assertEquals("랜턴 B", result.get(1).getName());
		assertEquals("랜턴 C", result.get(2).getName());
	}
}
