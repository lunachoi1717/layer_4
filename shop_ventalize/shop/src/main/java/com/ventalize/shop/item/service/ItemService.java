package com.ventalize.shop.item.service;

import com.ventalize.shop.item.dto.ItemRead;
import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemRead> findAll();
    List<ItemRead> findAll(List<Integer> ids);
    List<ItemRead> findByCategory(List<String> cts);
    Optional<ItemRead> findById(Integer id);
    List<ItemRead> findNew();
    List<ItemRead> findBest();
    List<ItemRead> findRecommend();
    List<ItemRead> search(String keyword);
    List<ItemRead> findRelated(String category, Integer excludeId);
}
