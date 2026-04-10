package com.ventalize.shop.controller;

import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.dto.coupon.CouponRead;
import com.ventalize.shop.repository.CouponRepository;
import com.ventalize.shop.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;
    private final SecurityUtil securityUtil;

    @GetMapping("/my")
    public ResponseEntity<?> myCoupons() {
        Member member = securityUtil.getCurrentMember();
        if (member == null) return ResponseEntity.status(401).build();

        String grade = member.getGrade() != null ? member.getGrade() : "SAPPHIRE";
        List<CouponRead> coupons = couponRepository
                .findAvailableByGrade(grade, LocalDate.now())
                .stream()
                .map(CouponRead::from)
                .toList();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkCoupon(@RequestParam String code) {
        Member member = securityUtil.getCurrentMember();
        if (member == null) return ResponseEntity.status(401).build();

        return couponRepository.findByCode(code)
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .filter(c -> !LocalDate.now().isBefore(c.getValidFrom()) && !LocalDate.now().isAfter(c.getValidTo()))
                .filter(c -> c.getTargetGrade() == null || c.getTargetGrade().equals(member.getGrade()))
                .map(c -> ResponseEntity.ok((Object) CouponRead.from(c)))
                .orElse(ResponseEntity.status(404).body("사용 불가능한 쿠폰입니다."));
    }
}
