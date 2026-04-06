package com.ventalize.shop.cart.controller;

import com.ventalize.shop.cart.dto.CartItemResponse;
import com.ventalize.shop.cart.dto.CartRead;
import com.ventalize.shop.cart.dto.CartRequest;
import com.ventalize.shop.cart.service.CartService;
import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.item.dto.ItemRead;
import com.ventalize.shop.item.service.ItemService;
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
        if (existing == null) {
            cartService.save(cartReq.toEntity(memberId));
        } else {
            return ResponseEntity.status(409).body("이미 장바구니에 담긴 상품입니다.");
        }
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
