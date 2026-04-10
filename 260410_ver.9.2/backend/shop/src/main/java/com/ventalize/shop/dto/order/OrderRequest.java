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
    /** itemId → 주문 수량 (미전송 시 각 1개로 처리) */
    private Map<Integer, Integer> quantities;
    /** 적용 쿠폰 코드 (없으면 null) */
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
