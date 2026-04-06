package com.ventalize.shop.cart.dto;

import com.ventalize.shop.cart.entity.Cart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {

    private Integer itemId;

    // 엔티티 객체로 변환
    public Cart toEntity(Integer memberId) {
        return new Cart(memberId, itemId);
    }
}
