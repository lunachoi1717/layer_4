package com.ventalize.shop.admin.controller;

import com.ventalize.shop.board.repository.InquiryRepository;
import com.ventalize.shop.item.repository.ItemRepository;
import com.ventalize.shop.member.repository.MemberRepository;
import com.ventalize.shop.order.repository.OrderRepository;
import com.ventalize.shop.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final InquiryRepository inquiryRepository;

    @GetMapping
    public ResponseEntity<?> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalMembers", memberRepository.count());
        data.put("totalItems", itemRepository.count());
        data.put("totalOrders", orderRepository.count());
        data.put("pendingPaymentOrders", orderRepository.countByStatus("PENDING_PAYMENT"));
        data.put("paidOrders", orderRepository.countByStatus("PAID"));
        data.put("shippingOrders", orderRepository.countByStatus("SHIPPING"));
        data.put("deliveredOrders", orderRepository.countByStatus("DELIVERED"));
        data.put("cancelledOrders", orderRepository.countByStatus("CANCELLED"));
        data.put("totalReviews", reviewRepository.count());
        data.put("unansweredInquiries", inquiryRepository.countByIsAnsweredFalse());

        // 총 매출 계산 (결제완료 + 배송중 + 배송완료 기준)
        long totalSales = orderRepository.findAll().stream()
                .filter(o -> List.of("PAID","SHIPPING","DELIVERED").contains(o.getStatus()))
                .mapToLong(o -> o.getAmount() != null ? o.getAmount() : 0L)
                .sum();
        data.put("totalSales", totalSales);

        // 재고 부족 상품 (5개 이하)
        data.put("lowStockItems", itemRepository.findAll().stream()
                .filter(i -> i.getStockCount() != null && i.getStockCount() <= 5)
                .map(i -> Map.of("id", i.getId(), "name", i.getName(), "stockCount", i.getStockCount()))
                .toList());

        // 최근 주문 5건
        data.put("recentOrders", orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(5).map(o -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", o.getId());
                    m.put("name", o.getName());
                    m.put("amount", o.getAmount());
                    m.put("status", o.getStatus());
                    m.put("statusLabel", o.getStatusLabel());
                    m.put("createdAt", o.getCreatedAt());
                    return m;
                }).toList());

        return ResponseEntity.ok(data);
    }
}
