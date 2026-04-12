package com.ventalize.shop.controller;

import com.ventalize.shop.dto.item.ItemCreateRequest;
import com.ventalize.shop.entity.Item;
import com.ventalize.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemRepository itemRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.images-dir}")
    private String imagesDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("imgPath", "/v1/images/" + filename));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/images")
    public ResponseEntity<?> listImages() {
        try {
            Path dir = Paths.get(imagesDir).toAbsolutePath().normalize();
            if (!Files.exists(dir)) return ResponseEntity.ok(List.of());
            List<String> files = Files.list(dir)
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.matches("(?i).*\\.(png|jpg|jpeg|gif|webp)$"))
                    .sorted()
                    .toList();
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String keyword) {
        List<Item> items;
        if (StringUtils.hasLength(keyword)) {
            items = itemRepository.findByNameContaining(keyword);
        } else {
            items = itemRepository.findAllByOrderByIdDesc();
        }
        return ResponseEntity.ok(items.stream().map(Item::toRead).toList());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody ItemCreateRequest req) {
        String imgPath = StringUtils.hasLength(req.getImgPath())
                ? req.getImgPath()
                : "/images/default.png";

        Item item = Item.builder()
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody ItemCreateRequest req) {
        return itemRepository.findById(id).map(item -> {
            if (StringUtils.hasLength(req.getName()))        item.setName(req.getName());
            if (StringUtils.hasLength(req.getCategory()))    item.setCategory(req.getCategory());
            if (StringUtils.hasLength(req.getDescription())) item.setDescription(req.getDescription());
            if (req.getPrice() != null)                      item.setPrice(req.getPrice());
            if (req.getDiscountPer() != null)                item.setDiscountPer(req.getDiscountPer());
            if (req.getStockCount() != null)                 item.setStockCount(req.getStockCount());
            if (StringUtils.hasLength(req.getImgPath()))     item.setImgPath(req.getImgPath());
            return ResponseEntity.ok(itemRepository.save(item).toRead());
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return itemRepository.findById(id).map(item -> {
            itemRepository.delete(item);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }

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
