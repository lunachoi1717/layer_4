package com.ventalize.shop.service;

import com.ventalize.shop.dto.item.ItemRead;
import com.ventalize.shop.dto.order.OrderItemDetail;
import com.ventalize.shop.dto.order.OrderRead;
import com.ventalize.shop.dto.order.OrderRequest;
import com.ventalize.shop.entity.Item;
import com.ventalize.shop.entity.Order;
import com.ventalize.shop.entity.OrderItem;
import com.ventalize.shop.repository.ItemRepository;
import com.ventalize.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final CartService cartService;
    private final PaymentSimulator paymentSimulator;

    // 주문 목록 조회 (아이템 상세 포함)
    public List<OrderRead> findAll(Integer memberId) {
        return orderRepository.findAllByMemberIdOrderByIdDesc(memberId).stream()
                .map(o -> {
                    OrderRead read = o.toRead();
                    read.setItems(buildDetails(o.getId()));
                    return read;
                })
                .toList();
    }

    // 주문 상세 조회 (아이템 상세 포함)
    public OrderRead find(Integer id, Integer memberId) {
        Optional<Order> orderOptional = orderRepository.findByIdAndMemberId(id, memberId);
        if (orderOptional.isPresent()) {
            OrderRead order = orderOptional.get().toRead();
            order.setItems(buildDetails(order.getId()));
            return order;
        }
        return null;
    }

    // 주문 내용 저장 (재고 검증 → 금액 계산 → 저장 → 재고 차감)
    @Transactional
    public void order(OrderRequest orderReq, Integer memberId) {
        List<Integer> itemIds = orderReq.getItemIds();
        Map<Integer, Integer> quantities = orderReq.getQuantities() != null
                ? orderReq.getQuantities() : Map.of();

        // 1. 재고 검증 (주문 전 모든 상품 재고 확인)
        for (Integer itemId : itemIds.stream().distinct().toList()) {
            int qty = quantities.getOrDefault(itemId, 1);
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + itemId));
            if (item.getStockCount() == null || item.getStockCount() < qty) {
                throw new IllegalStateException("재고 부족: " + item.getName()
                        + " (요청 " + qty + "개 / 재고 " + item.getStockCount() + "개)");
            }
        }

        // 2. 최종 결제 금액 계산 (수량 반영)
        List<ItemRead> itemReads = itemService.findAll(itemIds);
        Map<Integer, ItemRead> itemMap = itemReads.stream()
                .collect(Collectors.toMap(ItemRead::getId, i -> i));

        long amount = 0L;
        for (Integer itemId : itemIds.stream().distinct().toList()) {
            int qty = quantities.getOrDefault(itemId, 1);
            ItemRead ir = itemMap.get(itemId);
            if (ir != null) {
                amount += (long) ir.getSalePrice() * qty;
            }
        }
        orderReq.setAmount(amount);

        // 3. 주문 저장
        Order order = orderRepository.save(orderReq.toEntity(memberId));

        // 4. 주문 상품 데이터 저장 (수량 포함)
        List<OrderItem> newOrderItems = new ArrayList<>();
        for (Integer itemId : itemIds.stream().distinct().toList()) {
            int qty = quantities.getOrDefault(itemId, 1);
            newOrderItems.add(new OrderItem(order.getId(), itemId, qty));
        }
        orderItemService.saveAll(newOrderItems);

        // 5. 재고 차감
        for (Integer itemId : itemIds.stream().distinct().toList()) {
            int qty = quantities.getOrDefault(itemId, 1);
            itemRepository.findById(itemId).ifPresent(item -> {
                int updated = Math.max(0, item.getStockCount() - qty);
                item.setStockCount(updated);
                itemRepository.save(item);
            });
        }

        // 6. 장바구니 비우기
        cartService.removeAll(order.getMemberId());

        // 7. 결제 시뮬레이션 (10초 후 PAID 전환)
        paymentSimulator.simulatePayment(order.getId());
    }

    /** 주문 ID로 OrderItemDetail 목록 생성 */
    public List<OrderItemDetail> buildDetails(Integer orderId) {
        List<OrderItem> orderItems = orderItemService.findAll(orderId);
        if (orderItems.isEmpty()) return List.of();

        List<Integer> ids = orderItems.stream().map(OrderItem::getItemId).toList();
        List<ItemRead> itemReads = itemService.findAll(ids);
        Map<Integer, ItemRead> itemMap = itemReads.stream()
                .collect(Collectors.toMap(ItemRead::getId, i -> i));

        return orderItems.stream().map(oi -> {
            ItemRead ir = itemMap.get(oi.getItemId());
            int qty = oi.getQuantity() != null ? oi.getQuantity() : 1;
            int salePrice = (ir != null && ir.getSalePrice() != null) ? ir.getSalePrice() : 0;
            return OrderItemDetail.builder()
                    .itemId(oi.getItemId())
                    .itemName(ir != null ? ir.getName() : "(삭제된 상품)")
                    .imgPath(ir != null ? ir.getImgPath() : null)
                    .quantity(qty)
                    .unitPrice(ir != null ? ir.getPrice() : 0)
                    .salePrice(salePrice)
                    .subtotal(salePrice * qty)
                    .build();
        }).toList();
    }
}
