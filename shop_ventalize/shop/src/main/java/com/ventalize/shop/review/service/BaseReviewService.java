package com.ventalize.shop.review.service;

import com.ventalize.shop.member.entity.Member;
import com.ventalize.shop.member.repository.MemberRepository;
import com.ventalize.shop.review.dto.ReviewRead;
import com.ventalize.shop.review.dto.ReviewRequest;
import com.ventalize.shop.review.entity.Review;
import com.ventalize.shop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    private ReviewRead toRead(Review r) {
        String memberName = memberRepository.findById(r.getMemberId())
                .map(Member::getName).orElse("(탈퇴회원)");
        return ReviewRead.builder()
                .id(r.getId())
                .memberId(r.getMemberId())
                .memberName(memberName)
                .itemId(r.getItemId())
                .orderId(r.getOrderId())
                .rating(r.getRating())
                .content(r.getContent())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    @Override
    public List<ReviewRead> findByItem(Integer itemId) {
        return reviewRepository.findAllByItemIdOrderByCreatedAtDesc(itemId)
                .stream().map(this::toRead).toList();
    }

    @Override
    public List<ReviewRead> findByMember(Integer memberId) {
        return reviewRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(this::toRead).toList();
    }

    @Override
    public ReviewRead save(ReviewRequest req, Integer memberId) {
        Review review = Review.builder()
                .memberId(memberId)
                .itemId(req.getItemId())
                .orderId(req.getOrderId())
                .rating(req.getRating())
                .content(req.getContent())
                .build();
        return toRead(reviewRepository.save(review));
    }

    @Override
    public ReviewRead update(Integer id, ReviewRequest req, Integer memberId) {
        return reviewRepository.findByIdAndMemberId(id, memberId).map(r -> {
            if (req.getRating() != null) r.setRating(req.getRating());
            if (req.getContent() != null) r.setContent(req.getContent());
            return toRead(reviewRepository.save(r));
        }).orElse(null);
    }

    @Override
    public boolean delete(Integer id, Integer memberId) {
        return reviewRepository.findByIdAndMemberId(id, memberId).map(r -> {
            reviewRepository.delete(r);
            return true;
        }).orElse(false);
    }

    @Override
    public List<ReviewRead> findAll() {
        return reviewRepository.findAll().stream().map(this::toRead).toList();
    }

    @Override
    public boolean adminDelete(Integer id) {
        return reviewRepository.findById(id).map(r -> {
            reviewRepository.delete(r);
            return true;
        }).orElse(false);
    }
}
