package com.ventalize.shop.order.dto;

import com.ventalize.shop.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private String name;
    private String address;
    private String payment;
    private String cardNumber;
    private Long amount;
    private List<Integer> itemIds;

    public Order toEntity(Integer memberId) {
        return Order.builder()
                .memberId(memberId)
                .name(name)
                .address(address)
                .payment(payment)
                .cardNumber(cardNumber)
                .amount(amount)
                .build();
    }
}
