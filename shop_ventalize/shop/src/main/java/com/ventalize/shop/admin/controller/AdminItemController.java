package com.ventalize.shop.admin.controller;

import com.ventalize.shop.common.config.FileStorageService;
import com.ventalize.shop.item.dto.ItemCreateRequest;
import com.ventalize.shop.item.dto.ItemRead;
import com.ventalize.shop.item.entity.Item;
import com.ventalize.shop.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    /** 전체 상품 목록 */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String keyword) {
        List<Item> items;
        if (StringUtils.hasLength(keyword)) {
            items = itemRepository.findByNameContainingOrBrandContaining(keyword, keyword);
        } else {
            items = itemRepository.findAll();
        }
        return ResponseEntity.ok(items.stream().map(Item::toRead).toList());
    }

    /** 상품 등록 (이미지 포함) */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> add(
            @RequestPart("data") ItemCreateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        String imgPath = (image != null && !image.isEmpty())
                ? fileStorageService.store(image)
                : "https://picsum.photos/seed/new/400/500";

        Item item = Item.builder()
                .brand(req.getBrand())
                .name(req.getName())
                .category(req.getCategory())
                .description(req.getDescription())
                .price(req.getPrice())
                .discountPer(req.getDiscountPer() != null ? req.getDiscountPer() : 0)
                .stockCount(req.getStockCount() != null ? req.getStockCount() : 0)
                .viewCount(0)
                .imgPath(imgPath)
                .build();

        return ResponseEntity.ok(itemRepository.save(item).toRead());
    }

    /** 상품 수정 */
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestPart("data") ItemCreateRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return itemRepository.findById(id).map(item -> {
            if (StringUtils.hasLength(req.getBrand())) item.setBrand(req.getBrand());
            if (StringUtils.hasLength(req.getName())) item.setName(req.getName());
            if (StringUtils.hasLength(req.getCategory())) item.setCategory(req.getCategory());
            if (StringUtils.hasLength(req.getDescription())) item.setDescription(req.getDescription());
            if (req.getPrice() != null) item.setPrice(req.getPrice());
            if (req.getDiscountPer() != null) item.setDiscountPer(req.getDiscountPer());
            if (req.getStockCount() != null) item.setStockCount(req.getStockCount());

            if (image != null && !image.isEmpty()) {
                fileStorageService.delete(item.getImgPath());
                item.setImgPath(fileStorageService.store(image));
            }
            return ResponseEntity.ok(itemRepository.save(item).toRead());
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 상품 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return itemRepository.findById(id).map(item -> {
            fileStorageService.delete(item.getImgPath());
            itemRepository.delete(item);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 재고 수정 */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        Integer stock = body.get("stockCount");
        if (stock == null || stock < 0) return ResponseEntity.badRequest().body("유효하지 않은 재고 수량입니다.");
        return itemRepository.findById(id).map(item -> {
            item.setStockCount(stock);
            return ResponseEntity.ok(itemRepository.save(item).toRead());
        }).orElse(ResponseEntity.notFound().build());
    }
}
