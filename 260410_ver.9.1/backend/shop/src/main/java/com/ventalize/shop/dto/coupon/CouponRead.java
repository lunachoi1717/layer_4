package com.ventalize.shop.dto.coupon;

import com.ventalize.shop.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CouponRead {
    private Integer id;
    private String name;
    private String code;
    private String discountType;
    private Integer discountValue;
    private String targetGrade;
    private Long minOrderAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean isActive;

    public static CouponRead from(Coupon c) {
        return CouponRead.builder()
                .id(c.getId())
                .name(c.getName())
                .code(c.getCode())
                .discountType(c.getDiscountType())
                .discountValue(c.getDiscountValue())
                .targetGrade(c.getTargetGrade())
                .minOrderAmount(c.getMinOrderAmount())
                .validFrom(c.getValidFrom())
                .validTo(c.getValidTo())
                .isActive(c.getIsActive())
                .build();
    }
}
