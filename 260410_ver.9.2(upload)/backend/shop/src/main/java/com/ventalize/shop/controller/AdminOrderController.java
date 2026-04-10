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

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String status) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        if (status != null && !status.isBlank()) {
            orders = orders.stream().filter(o -> status.equals(o.getStatus())).toList();
        }
        return ResponseEntity.ok(orders.stream().map(this::toRead).toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");

        if (!List.of("PENDING_PAYMENT", "PAID", "SHIPPING", "DELIVERED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().body("유효하지 않은 상태입니다.");
        }

        try {
            OrderRead result = orderService.changeOrderStatus(id, newStatus);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
