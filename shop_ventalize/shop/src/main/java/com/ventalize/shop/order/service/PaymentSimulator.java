package com.ventalize.shop.order.service;

import com.ventalize.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 시뮬레이터 — 주문 생성 10초 후 상태를 PAID로 자동 전환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSimulator {

    private final OrderRepository orderRepository;

    @Async
    @Transactional
    public void simulatePayment(Integer orderId) {
        try {
            Thread.sleep(10_000);
            orderRepository.findById(orderId).ifPresent(order -> {
                if ("PENDING_PAYMENT".equals(order.getStatus())) {
                    order.setStatus("PAID");
                    orderRepository.save(order);
                    log.info("[PaymentSimulator] 주문 #{} PENDING_PAYMENT → PAID 자동 전환", orderId);
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[PaymentSimulator] 주문 #{} 결제 시뮬레이션 인터럽트", orderId);
        }
    }
}
