package com.ventalize.shop.order.controller;

import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.order.dto.OrderRead;
import com.ventalize.shop.order.dto.OrderRequest;
import com.ventalize.shop.order.entity.Order;
import com.ventalize.shop.order.repository.OrderRepository;
import com.ventalize.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<?> readAll() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        List<OrderRead> orders = orderService.findAll(memberId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        OrderRead order = orderService.find(id, memberId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody OrderRequest orderReq) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        orderService.order(orderReq, memberId);
        return ResponseEntity.ok().build();
    }

    /** 주문 취소 */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        Order order = orderRepository.findByIdAndMemberId(id, memberId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        if ("CANCELLED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("이미 취소된 주문입니다.");
        }
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }
}
