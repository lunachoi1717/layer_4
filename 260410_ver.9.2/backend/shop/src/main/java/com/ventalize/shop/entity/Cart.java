package com.ventalize.shop.entity;

import com.ventalize.shop.dto.cart.CartRead;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer memberId;

    @Column(nullable = false)
    private Integer itemId;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer quantity = 1;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Cart() {
    }

    public Cart(Integer memberId, Integer itemId) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.quantity = 1;
    }

    public Cart(Integer memberId, Integer itemId, Integer qty) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.quantity = (qty != null && qty > 0) ? qty : 1;
    }

    public void incrementQuantity() {
        this.quantity = Objects.requireNonNullElse(this.quantity, 1) + 1;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = Math.max(1, quantity);
    }

    // 장바구니 조회 DTO로 변환
    public CartRead toRead() {
        return CartRead.builder()
                .id(id)
                .itemId(itemId)
                .quantity(Objects.requireNonNullElse(quantity, 1))
                .build();
    }
}
