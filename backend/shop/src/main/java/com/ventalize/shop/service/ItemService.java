package com.ventalize.shop.service;

import com.ventalize.shop.dto.item.ItemRead;
import com.ventalize.shop.entity.Item;
import com.ventalize.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemRead> findAll() {
        return itemRepository.findAllByOrderByIdAsc().stream().map(Item::toRead).toList();
    }

    public List<ItemRead> findAll(List<Integer> ids) {
        return itemRepository.findAllByIdIn(ids).stream().map(Item::toRead).toList();
    }

    public List<ItemRead> findByCategory(List<String> cts) {
        return itemRepository.findAllByCategoryInOrderByIdAsc(cts).stream().map(Item::toRead).toList();
    }

    public Optional<ItemRead> findById(Integer id) {
        return itemRepository.findById(id).map(item -> {
            item.setViewCount(item.getViewCount() == null ? 1 : item.getViewCount() + 1);
            itemRepository.save(item);
            return item.toRead();
        });
    }

    public List<ItemRead> findNew() {
        return itemRepository.findTop5ByOrderByCreatedAtDesc().stream().map(Item::toRead).toList();
    }

    public List<ItemRead> findBest() {
        return itemRepository.findTop5ByOrderByViewCountDesc().stream().map(Item::toRead).toList();
    }

    public List<ItemRead> findRecommend() {
        return itemRepository.findTop5ByDiscountPerGreaterThanOrderByDiscountPerDesc(0)
                .stream().map(Item::toRead).toList();
    }

    public List<ItemRead> search(String keyword) {
        return itemRepository.findByNameContainingOrderByIdAsc(keyword)
                .stream().map(Item::toRead).toList();
    }

    public List<ItemRead> findRelated(String category, Integer excludeId) {
        return itemRepository.findByCategoryAndIdNotOrderByIdAsc(category, excludeId)
                .stream().limit(4).map(Item::toRead).toList();
    }
}
