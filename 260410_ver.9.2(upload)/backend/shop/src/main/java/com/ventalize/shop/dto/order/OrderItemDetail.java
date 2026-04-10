package com.ventalize.shop.dto.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemDetail {
    private Integer itemId;
    private String  itemName;
    private String  imgPath;
    private Integer quantity;
    private Integer unitPrice;
    private Integer salePrice;
    private Integer subtotal;
}
