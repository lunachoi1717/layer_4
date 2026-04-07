package com.ventalize.shop.dto.item;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemRead {
    private final Integer id;
    private final String name;
    private final String category;
    private final String imgPath;
    private final String description;
    private final Integer price;
    private final Integer discountPer;
    private final Integer salePrice;
    private final Integer stockCount;
    private final Integer viewCount;
    private final boolean isSoldOut;
    private final LocalDateTime createdAt;
}
