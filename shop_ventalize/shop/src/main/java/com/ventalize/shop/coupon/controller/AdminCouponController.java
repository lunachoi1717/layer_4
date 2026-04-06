package com.ventalize.shop.coupon.controller;

import com.ventalize.shop.coupon.dto.CouponCreateRequest;
import com.ventalize.shop.coupon.dto.CouponRead;
import com.ventalize.shop.coupon.entity.Coupon;
import com.ventalize.shop.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자용 쿠폰 CRUD API
 */
@RestController
@RequestMapping("/v1/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponRepository couponRepository;

    /** 전체 쿠폰 목록 */
    @GetMapping
    public ResponseEntity<List<CouponRead>> list() {
        return ResponseEntity.ok(
                couponRepository.findAllByOrderByCreatedAtDesc()
                        .stream().map(CouponRead::from).toList()
        );
    }

    /** 쿠폰 생성 */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CouponCreateRequest req) {
        if (couponRepository.existsByCode(req.getCode())) {
            return ResponseEntity.status(409).body("이미 사용 중인 쿠폰 코드입니다.");
        }
        Coupon coupon = Coupon.builder()
                .name(req.getName())
                .code(req.getCode().toUpperCase())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .targetGrade(req.getTargetGrade())
                .minOrderAmount(req.getMinOrderAmount() != null ? req.getMinOrderAmount() : 0L)
                .validFrom(req.getValidFrom())
                .validTo(req.getValidTo())
                .build();
        return ResponseEntity.ok(CouponRead.from(couponRepository.save(coupon)));
    }

    /** 쿠폰 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody CouponCreateRequest req) {
        return couponRepository.findById(id)
                .map(c -> {
                    if (req.getName() != null) c.setName(req.getName());
                    if (req.getDiscountType() != null) c.setDiscountType(req.getDiscountType());
                    if (req.getDiscountValue() != null) c.setDiscountValue(req.getDiscountValue());
                    c.setTargetGrade(req.getTargetGrade());
                    if (req.getMinOrderAmount() != null) c.setMinOrderAmount(req.getMinOrderAmount());
                    if (req.getValidFrom() != null) c.setValidFrom(req.getValidFrom());
                    if (req.getValidTo() != null) c.setValidTo(req.getValidTo());
                    return ResponseEntity.ok((Object) CouponRead.from(couponRepository.save(c)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** 쿠폰 활성/비활성 토글 */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Integer id) {
        return couponRepository.findById(id)
                .map(c -> {
                    c.setIsActive(!c.getIsActive());
                    return ResponseEntity.ok((Object) CouponRead.from(couponRepository.save(c)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** 쿠폰 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (!couponRepository.existsById(id)) return ResponseEntity.notFound().build();
        couponRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
