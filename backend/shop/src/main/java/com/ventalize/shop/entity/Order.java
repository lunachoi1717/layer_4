package com.ventalize.shop.entity;

import com.ventalize.shop.dto.order.OrderRead;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer memberId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 20)
    private String payment;

    @Column(length = 19)
    private String cardNumber;

    @Column(nullable = false)
    private Long amount;

    /**
     * 배송 현황 상태값
     * PENDING_PAYMENT : 결제대기
     * PAID            : 결제완료
     * SHIPPING        : 배송중
     * DELIVERED       : 배송완료
     * CANCELLED       : 취소
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING_PAYMENT";

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public OrderRead toRead() {
        return OrderRead.builder()
                .id(id)
                .name(name)
                .address(address)
                .payment(payment)
                .amount(amount)
                .status(status)
                .statusLabel(getStatusLabel())
                .createdAt(createdAt)
                .build();
    }

    public String getStatusLabel() {
        return switch (status) {
            case "PENDING_PAYMENT" -> "결제대기";
            case "PAID"            -> "결제완료";
            case "SHIPPING"        -> "배송중";
            case "DELIVERED"       -> "배송완료";
            case "CANCELLED"       -> "취소";
            default                -> status;
        };
    }
}
