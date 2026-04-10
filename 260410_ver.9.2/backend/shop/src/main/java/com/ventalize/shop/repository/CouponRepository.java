package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    boolean existsByCode(String code);

    Optional<Coupon> findByCode(String code);

    /** 특정 회원 등급에서 사용 가능한 쿠폰 (등급 전용 or 전체 대상 + 유효 기간 내 + 활성) */
    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :today AND c.validTo >= :today " +
           "AND (c.targetGrade IS NULL OR c.targetGrade = :grade)")
    List<Coupon> findAvailableByGrade(@Param("grade") String grade, @Param("today") LocalDate today);

    List<Coupon> findAllByOrderByCreatedAtDesc();
}
