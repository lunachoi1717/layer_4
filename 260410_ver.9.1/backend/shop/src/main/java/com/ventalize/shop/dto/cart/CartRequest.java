package com.ventalize.shop.dto.cart;

import com.ventalize.shop.entity.Cart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {

    private Integer itemId;
    private Integer qty; // 담을 수량 (미전송 시 1)

    // 엔티티 객체로 변환
    public Cart toEntity(Integer memberId) {
        int quantity = (qty != null && qty > 0) ? qty : 1;
        return new Cart(memberId, itemId, quantity);
    }
}
