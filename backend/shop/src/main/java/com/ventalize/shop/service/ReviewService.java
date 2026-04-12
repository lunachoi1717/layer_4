package com.ventalize.shop.service;

import com.ventalize.shop.entity.Item;
import com.ventalize.shop.entity.Member;
import com.ventalize.shop.entity.Order;
import com.ventalize.shop.repository.ItemRepository;
import com.ventalize.shop.repository.MemberRepository;
import com.ventalize.shop.repository.OrderItemRepository;
import com.ventalize.shop.repository.OrderRepository;
import com.ventalize.shop.dto.review.ReviewRead;
import com.ventalize.shop.dto.review.ReviewRequest;
import com.ventalize.shop.entity.Review;
import com.ventalize.shop.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private ReviewRead toRead(Review r) {
        String memberName = memberRepository.findById(r.getMemberId())
                .map(Member::getName).orElse("(탈퇴회원)");
        Item item = itemRepository.findById(r.getItemId()).orElse(null);
        String itemName = item != null ? item.getName() : "(삭제된 상품)";
        String imgPath  = item != null ? item.toRead().getImgPath() : null;
        return ReviewRead.builder()
                .id(r.getId())
                .memberId(r.getMemberId())
                .memberName(memberName)
                .itemId(r.getItemId())
                .itemName(itemName)
                .imgPath(imgPath)
                .orderId(r.getOrderId())
                .rating(r.getRating())
                .content(r.getContent())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    public List<ReviewRead> findByItem(Integer itemId) {
        return reviewRepository.findAllByItemIdOrderByCreatedAtDesc(itemId)
                .stream().map(this::toRead).toList();
    }

    public List<ReviewRead> findByMember(Integer memberId) {
        return reviewRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(this::toRead).toList();
    }

    public ReviewRead save(ReviewRequest req, Integer memberId) {
        // 1. orderId 필수 확인
        if (req.getOrderId() == null) {
            throw new IllegalArgumentException("주문 정보가 필요합니다.");
        }

        // 2. 해당 주문이 현재 회원의 주문인지 확인
        Order order = orderRepository.findByIdAndMemberId(req.getOrderId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 3. 배송완료 상태 확인
        if (!"DELIVERED".equals(order.getStatus())) {
            throw new IllegalStateException("배송완료된 주문에만 리뷰를 작성할 수 있습니다.");
        }

        // 4. 해당 주문에 해당 상품이 포함되어 있는지 확인
        if (!orderItemRepository.existsByOrderIdAndItemId(req.getOrderId(), req.getItemId())) {
            throw new IllegalArgumentException("해당 주문에 포함되지 않은 상품입니다.");
        }

        // 5. 동일 주문+상품에 대해 중복 리뷰 방지
        if (reviewRepository.existsByMemberIdAndOrderIdAndItemId(memberId, req.getOrderId(), req.getItemId())) {
            throw new IllegalStateException("이미 리뷰를 작성하셨습니다.");
        }

        Review review = Review.builder()
                .memberId(memberId)
                .itemId(req.getItemId())
                .orderId(req.getOrderId())
                .rating(req.getRating())
                .content(req.getContent())
                .build();
        return toRead(reviewRepository.save(review));
    }

    public ReviewRead update(Integer id, ReviewRequest req, Integer memberId) {
        return reviewRepository.findByIdAndMemberId(id, memberId).map(r -> {
            if (req.getRating() != null) r.setRating(req.getRating());
            if (req.getContent() != null) r.setContent(req.getContent());
            return toRead(reviewRepository.save(r));
        }).orElse(null);
    }

    public boolean delete(Integer id, Integer memberId) {
        return reviewRepository.findByIdAndMemberId(id, memberId).map(r -> {
            reviewRepository.delete(r);
            return true;
        }).orElse(false);
    }

    public List<ReviewRead> findAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toRead).toList();
    }

    public boolean adminDelete(Integer id) {
        return reviewRepository.findById(id).map(r -> {
            reviewRepository.delete(r);
            return true;
        }).orElse(false);
    }
}
