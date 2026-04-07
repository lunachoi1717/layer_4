package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByIdIn(List<Integer> ids);

    List<Item> findAllByCategoryIn(List<String> cts);

    List<Item> findTop4ByOrderByCreatedAtDesc();

    List<Item> findTop4ByOrderByViewCountDesc();

    List<Item> findTop4ByDiscountPerGreaterThanOrderByDiscountPerDesc(int discountPer);

    List<Item> findByCategoryAndIdNot(String category, Integer id);

    List<Item> findByNameContaining(String name);
}
