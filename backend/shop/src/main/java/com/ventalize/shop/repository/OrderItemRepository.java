package com.ventalize.shop.repository;

import com.ventalize.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findAllByOrderId(Integer orderId);

    boolean existsByOrderIdAndItemId(Integer orderId, Integer itemId);
}
