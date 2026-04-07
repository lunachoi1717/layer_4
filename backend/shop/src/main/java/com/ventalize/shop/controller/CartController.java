package com.ventalize.shop.controller;

import com.ventalize.shop.dto.cart.CartItemResponse;
import com.ventalize.shop.dto.cart.CartRead;
import com.ventalize.shop.dto.cart.CartRequest;
import com.ventalize.shop.service.CartService;
import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.dto.item.ItemRead;
import com.ventalize.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;
    private final SecurityUtil securityUtil;

    @GetMapping("/api/cart/items")
    public ResponseEntity<?> readAll() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();

        List<CartRead> carts = cartService.findAll(memberId);
        List<Integer> itemIds = carts.stream().map(CartRead::getItemId).toList();
        List<ItemRead> items = itemService.findAll(itemIds);

        Map<Integer, ItemRead> itemMap = items.stream()
                .collect(Collectors.toMap(ItemRead::getId, i -> i));

        List<CartItemResponse> response = carts.stream()
                .map(c -> CartItemResponse.builder()
                        .id(c.getId())
                        .itemId(c.getItemId())
                        .quantity(c.getQuantity())
                        .item(itemMap.get(c.getItemId()))
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/carts")
    public ResponseEntity<?> push(@RequestBody CartRequest cartReq) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();

        CartRead existing = cartService.find(memberId, cartReq.getItemId());
        int reqQty = (cartReq.getQty() != null && cartReq.getQty() > 0) ? cartReq.getQty() : 1;
        if (existing == null) {
            cartService.save(cartReq.toEntity(memberId));
        } else {
            cartService.addQty(memberId, cartReq.getItemId(), reqQty);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/cart/item/{itemId}/qty")
    public ResponseEntity<?> updateQty(@PathVariable Integer itemId, @RequestBody Map<String, Integer> body) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        Integer qty = body.get("quantity");
        if (qty == null || qty < 1) return ResponseEntity.badRequest().build();
        cartService.updateQty(memberId, itemId, qty);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/cart/item/{itemId}")
    public ResponseEntity<?> remove(@PathVariable Integer itemId) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        cartService.remove(memberId, itemId);
        return ResponseEntity.ok().build();
    }
}
