package com.ventalize.shop.review.service;

import com.ventalize.shop.review.dto.ReviewRead;
import com.ventalize.shop.review.dto.ReviewRequest;
import com.ventalize.shop.review.entity.Review;

import java.util.List;

public interface ReviewService {
    List<ReviewRead> findByItem(Integer itemId);
    List<ReviewRead> findByMember(Integer memberId);
    ReviewRead save(ReviewRequest req, Integer memberId);
    ReviewRead update(Integer id, ReviewRequest req, Integer memberId);
    boolean delete(Integer id, Integer memberId);
    List<ReviewRead> findAll();
    boolean adminDelete(Integer id);
}
