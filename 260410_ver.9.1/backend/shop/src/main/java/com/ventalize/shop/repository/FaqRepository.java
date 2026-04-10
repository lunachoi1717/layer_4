package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findByIsPublishedTrueOrderByCreatedAtDesc();
    List<Faq> findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(String category);
    List<Faq> findAllByOrderByCreatedAtDesc();
}
