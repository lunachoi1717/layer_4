package com.ventalize.shop.controller;

import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.dto.order.OrderRead;
import com.ventalize.shop.dto.order.OrderRequest;
import com.ventalize.shop.entity.Order;
import com.ventalize.shop.repository.OrderRepository;
import com.ventalize.shop.service.OrderService;
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
        try {
            orderService.order(orderReq, memberId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {

            return ResponseEntity.status(409).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        Order order = orderRepository.findByIdAndMemberId(id, memberId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        if ("CANCELLED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("이미 취소된 주문입니다.");
        }
        orderService.restoreStock(id);
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }
}
