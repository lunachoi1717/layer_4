package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    List<Inquiry> findByMemberIdOrderByCreatedAtDesc(Integer memberId);
    List<Inquiry> findAllByOrderByCreatedAtDesc();
    long countByIsAnsweredFalse();
}
