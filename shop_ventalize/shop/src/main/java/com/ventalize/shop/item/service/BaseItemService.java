package com.ventalize.shop.item.service;

import com.ventalize.shop.item.dto.ItemRead;
import com.ventalize.shop.item.entity.Item;
import com.ventalize.shop.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseItemService implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<ItemRead> findAll() {
        return itemRepository.findAll().stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> findAll(List<Integer> ids) {
        return itemRepository.findAllByIdIn(ids).stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> findByCategory(List<String> cts) {
        return itemRepository.findAllByCategoryIn(cts).stream().map(Item::toRead).toList();
    }

    @Override
    public Optional<ItemRead> findById(Integer id) {
        return itemRepository.findById(id).map(item -> {
            item.setViewCount(item.getViewCount() == null ? 1 : item.getViewCount() + 1);
            itemRepository.save(item);
            return item.toRead();
        });
    }

    @Override
    public List<ItemRead> findNew() {
        return itemRepository.findTop4ByOrderByCreatedAtDesc().stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> findBest() {
        return itemRepository.findTop4ByOrderByViewCountDesc().stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> findRecommend() {
        return itemRepository.findTop4ByDiscountPerGreaterThanOrderByDiscountPerDesc(0)
                .stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> search(String keyword) {
        return itemRepository.findByNameContainingOrBrandContaining(keyword, keyword)
                .stream().map(Item::toRead).toList();
    }

    @Override
    public List<ItemRead> findRelated(String category, Integer excludeId) {
        return itemRepository.findByCategoryAndIdNot(category, excludeId)
                .stream().limit(4).map(Item::toRead).toList();
    }
}
