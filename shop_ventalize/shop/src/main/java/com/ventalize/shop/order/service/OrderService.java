package com.ventalize.shop.order.service;

import com.ventalize.shop.order.dto.OrderRead;
import com.ventalize.shop.order.dto.OrderRequest;

import java.util.List;

public interface OrderService {

    // 주문 목록 조회
    List<OrderRead> findAll(Integer memberId);

    // 주문 상세 조회
    OrderRead find(Integer id, Integer memberId);

    // 주문 내역 저장
    void order(OrderRequest orderReq, Integer memberId);
}
