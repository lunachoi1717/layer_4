package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByMemberIdOrderByIdDesc(Integer memberId);

    Optional<Order> findByIdAndMemberId(Integer id, Integer memberId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    /** 특정 회원의 since 이후 취소되지 않은 주문 누적 금액 */
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o " +
           "WHERE o.memberId = :memberId AND o.status != 'CANCELLED' AND o.createdAt >= :since")
    long sumAmountByMemberIdAndStatusNotCancelledSince(
            @Param("memberId") Integer memberId,
            @Param("since") LocalDateTime since);
}
