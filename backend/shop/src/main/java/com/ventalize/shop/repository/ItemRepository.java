package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByIdIn(List<Integer> ids);

    List<Item> findAllByOrderByIdAsc();

    List<Item> findAllByCategoryInOrderByIdAsc(List<String> cts);

    List<Item> findByNameContainingOrderByIdAsc(String name);

    List<Item> findTop5ByOrderByCreatedAtDesc();

    List<Item> findTop5ByOrderByViewCountDesc();

    List<Item> findTop5ByDiscountPerGreaterThanOrderByDiscountPerDesc(int discountPer);

    List<Item> findByCategoryAndIdNotOrderByIdAsc(String category, Integer id);

    List<Item> findByNameContaining(String name);

    List<Item> findAllByCategoryIn(List<String> cts);
}
