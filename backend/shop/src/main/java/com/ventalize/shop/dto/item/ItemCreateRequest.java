package com.ventalize.shop.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCreateRequest {
    private String name;
    private String category;
    private String imgPath;
    private String description;
    private Integer price;
    private Integer discountPer;
    private Integer stockCount;
}
