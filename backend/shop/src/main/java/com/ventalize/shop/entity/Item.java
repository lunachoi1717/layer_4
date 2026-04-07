package com.ventalize.shop.entity;

import com.ventalize.shop.dto.item.ItemRead;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 30)
    private String category;

    @Column(nullable = false, length = 255)
    private String imgPath;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer discountPer;

    @Column(name = "stock_count")
    @ColumnDefault("0")
    private Integer stockCount;

    @Column(name = "view_count")
    @ColumnDefault("0")
    private Integer viewCount;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public ItemRead toRead() {
        return ItemRead.builder()
                .id(id)
                .name(name)
                .category(category)
                .imgPath(normalizeImgPath(imgPath))
                .description(description)
                .price(price)
                .discountPer(discountPer)
                .salePrice(price * (100 - discountPer) / 100)
                .stockCount(stockCount)
                .viewCount(viewCount)
                .isSoldOut(stockCount != null && stockCount <= 0)
                .createdAt(createdAt)
                .build();
    }

    private String normalizeImgPath(String path) {
        if (path == null || path.isBlank()) {
            return "/images/default.png";
        }
        path = path.trim();
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        if (path.startsWith("/images/")) {
            return path;
        }
        if (path.startsWith("images/")) {
            return "/" + path;
        }
        return "/images/" + path;
    }
}
