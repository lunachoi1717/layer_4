package com.ventalize.shop.dto.coupon;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponCreateRequest {
    private String name;
    private String code;
    private String discountType;  // FIXED | PERCENT
    private Integer discountValue;
    private String targetGrade;   // null = 전체
    private Long minOrderAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
}
