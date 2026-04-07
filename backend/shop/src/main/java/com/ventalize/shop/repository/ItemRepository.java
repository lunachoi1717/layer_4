package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByIdIn(List<Integer> ids);

    List<Item> findAllByOrderByIdAsc();

    List<Item> findAllByCategoryInOrderByIdAsc(List<String> cts);

    List<Item> findByNameContainingOrderByIdAsc(String name);

    // 신상품 - 등록일 최신순 5개
    @Query("SELECT i FROM Item i ORDER BY i.createdAt DESC NULLS LAST")
    List<Item> findTop5NewItems(org.springframework.data.domain.Pageable pageable);

    // 베스트셀러 - 조회수 내림차순 5개 (NULL은 마지막)
    @Query("SELECT i FROM Item i ORDER BY COALESCE(i.viewCount, 0) DESC")
    List<Item> findTop5BestItems(org.springframework.data.domain.Pageable pageable);

    // 추천 - 할인율 내림차순 5개
    @Query("SELECT i FROM Item i ORDER BY COALESCE(i.discountPer, 0) DESC")
    List<Item> findTop5RecItems(org.springframework.data.domain.Pageable pageable);

    List<Item> findByCategoryAndIdNotOrderByIdAsc(String category, Integer id);

    List<Item> findByNameContaining(String name);

    List<Item> findAllByCategoryIn(List<String> cts);
}
