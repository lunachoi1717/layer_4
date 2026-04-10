package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findAllByItemIdOrderByCreatedAtDesc(Integer itemId);

    List<Review> findAllByMemberIdOrderByCreatedAtDesc(Integer memberId);

    Optional<Review> findByIdAndMemberId(Integer id, Integer memberId);

    boolean existsByMemberIdAndItemId(Integer memberId, Integer itemId);
}
