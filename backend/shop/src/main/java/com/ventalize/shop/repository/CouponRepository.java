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

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :today AND c.validTo >= :today " +
           "AND (c.targetGrade IS NULL OR c.targetGrade = :grade)")
    List<Coupon> findAvailableByGrade(@Param("grade") String grade, @Param("today") LocalDate today);

    List<Coupon> findAllByOrderByCreatedAtDesc();
}
