package com.ventalize.shop.controller;

import com.ventalize.shop.repository.MemberRepository;
import com.ventalize.shop.dto.order.OrderRead;
import com.ventalize.shop.entity.Order;
import com.ventalize.shop.repository.OrderRepository;
import com.ventalize.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderService orderService;

    /** 전체 주문 목록 (아이템 상세 포함) */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String status) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        if (status != null && !status.isBlank()) {
            orders = orders.stream().filter(o -> status.equals(o.getStatus())).toList();
        }
        return ResponseEntity.ok(orders.stream().map(this::toRead).toList());
    }

    /** 주문 상태 변경 */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (!List.of("PENDING_PAYMENT", "PAID", "SHIPPING", "DELIVERED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().body("유효하지 않은 상태입니다.");
        }
        return orderRepository.findById(id).map(o -> {
            o.setStatus(newStatus);
            return ResponseEntity.ok(toRead(orderRepository.save(o)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 판매 통계 */
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        List<Order> paid = orderRepository.findAllByOrderByCreatedAtDesc()
                .stream().filter(o -> "PAID".equals(o.getStatus())).toList();
        long totalSales = paid.stream().mapToLong(o -> o.getAmount() != null ? o.getAmount() : 0L).sum();
        return ResponseEntity.ok(Map.of(
                "count", paid.size(),
                "totalSales", totalSales
        ));
    }

    private OrderRead toRead(Order o) {
        String memberName = memberRepository.findById(o.getMemberId())
                .map(m -> m.getName()).orElse("(탈퇴회원)");
        OrderRead read = OrderRead.builder()
                .id(o.getId())
                .memberId(o.getMemberId())
                .memberName(memberName)
                .name(o.getName())
                .address(o.getAddress())
                .payment(o.getPayment())
                .amount(o.getAmount())
                .status(o.getStatus())
                .statusLabel(o.getStatusLabel())
                .createdAt(o.getCreatedAt())
                .build();
        read.setItems(orderService.buildDetails(o.getId()));
        return read;
    }
}
