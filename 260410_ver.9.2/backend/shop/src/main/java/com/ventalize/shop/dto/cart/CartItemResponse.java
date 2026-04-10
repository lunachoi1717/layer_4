package com.ventalize.shop.dto.cart;

import com.ventalize.shop.dto.item.ItemRead;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponse {

    private Integer id;       // cart row id
    private Integer itemId;
    private Integer quantity;
    private ItemRead item;
}
