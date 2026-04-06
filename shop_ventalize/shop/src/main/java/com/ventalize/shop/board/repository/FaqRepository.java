package com.ventalize.shop.board.repository;

import com.ventalize.shop.board.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findByIsPublishedTrueOrderByCreatedAtDesc();
    List<Faq> findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(String category);
    List<Faq> findAllByOrderByCreatedAtDesc();
}
