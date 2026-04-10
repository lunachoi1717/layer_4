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
    private Integer unitPrice;   // 원가
    private Integer salePrice;   // 할인가
    private Integer subtotal;    // salePrice * quantity
}
