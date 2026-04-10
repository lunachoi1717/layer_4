package com.ventalize.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 쿠폰 엔티티
 *
 * discountType: FIXED (정액 할인) | PERCENT (정률 할인)
 * targetGrade : null = 전체 회원, 값이 있으면 해당 등급 전용
 */
@Getter
@Setter
@Entity
@Table(name = "coupons")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 쿠폰 이름 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 쿠폰 코드 (고유) */
    @Column(nullable = false, length = 50, unique = true)
    private String code;

    /** 할인 방식: FIXED | PERCENT */
    @Column(nullable = false, length = 10)
    private String discountType;

    /** 할인 값 (정액 or 정률 %) */
    @Column(nullable = false)
    private Integer discountValue;

    /** 대상 등급 (null = 전체) */
    @Column(length = 20)
    private String targetGrade;

    /** 최소 주문 금액 (0 = 제한 없음) */
    @Column(nullable = false)
    @Builder.Default
    private Long minOrderAmount = 0L;

    /** 유효 시작일 */
    @Column(nullable = false)
    private LocalDate validFrom;

    /** 유효 종료일 */
    @Column(nullable = false)
    private LocalDate validTo;

    /** 활성 여부 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
