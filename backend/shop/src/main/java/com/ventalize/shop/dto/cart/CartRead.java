package com.ventalize.shop.dto.cart;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartRead {

    private Integer id;
    private Integer itemId;
    private Integer quantity;
}
