package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByIdIn(List<Integer> ids);

    List<Item> findAllByOrderByIdAsc();

    List<Item> findAllByOrderByIdDesc();

    List<Item> findAllByCategoryInOrderByIdAsc(List<String> cts);

    List<Item> findByNameContainingOrderByIdAsc(String name);

    @Query("SELECT i FROM Item i ORDER BY i.createdAt DESC NULLS LAST")
    List<Item> findTop5NewItems(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT i FROM Item i ORDER BY COALESCE(i.viewCount, 0) DESC")
    List<Item> findTop5BestItems(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT i FROM Item i ORDER BY COALESCE(i.discountPer, 0) DESC")
    List<Item> findTop5RecItems(org.springframework.data.domain.Pageable pageable);

    List<Item> findByCategoryAndIdNotOrderByIdAsc(String category, Integer id);

    List<Item> findByNameContaining(String name);

    List<Item> findAllByCategoryIn(List<String> cts);
}
