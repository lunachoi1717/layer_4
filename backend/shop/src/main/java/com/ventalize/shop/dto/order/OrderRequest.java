package com.ventalize.shop.dto.order;

import com.ventalize.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OrderRequest {

    private String name;
    private String address;
    private String payment;
    private String cardNumber;
    private Long amount;
    private List<Integer> itemIds;

    private Map<Integer, Integer> quantities;

    private String couponCode;

    public Order toEntity(Integer memberId) {
        return Order.builder()
                .memberId(memberId)
                .name(name)
                .address(address)
                .payment(payment)
                .cardNumber(cardNumber != null ? cardNumber.replace("-", "") : null)
                .amount(amount)
                .build();
    }
}
