package com.ventalize.shop.item.controller;

import com.ventalize.shop.common.config.FileStorageService;
import com.ventalize.shop.item.dto.ItemRead;
import com.ventalize.shop.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /** 상품 목록 (카테고리 필터, 정렬, 검색) */
    @GetMapping("/api/items")
    public ResponseEntity<?> readAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword) {

        if (StringUtils.hasLength(keyword)) {
            return ResponseEntity.ok(itemService.search(keyword));
        }
        if ("new".equals(sort)) return ResponseEntity.ok(itemService.findNew());
        if ("best".equals(sort)) return ResponseEntity.ok(itemService.findBest());
        if ("recommend".equals(sort)) return ResponseEntity.ok(itemService.findRecommend());

        List<ItemRead> items;
        if (StringUtils.hasLength(category)) {
            items = itemService.findByCategory(List.of(category));
        } else {
            items = itemService.findAll();
        }
        return ResponseEntity.ok(items);
    }

    /** 상품 상세 */
    @GetMapping("/api/items/{id}")
    public ResponseEntity<?> readOne(@PathVariable Integer id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 관련 상품 */
    @GetMapping("/api/items/{id}/related")
    public ResponseEntity<?> related(@PathVariable Integer id) {
        return itemService.findById(id).map(item ->
                ResponseEntity.ok(itemService.findRelated(item.getCategory(), id))
        ).orElse(ResponseEntity.notFound().build());
    }

    /** 업로드된 이미지 서빙 */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) return ResponseEntity.notFound().build();

            String contentType = "image/jpeg";
            if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
            else if (filename.toLowerCase().endsWith(".gif")) contentType = "image/gif";
            else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
