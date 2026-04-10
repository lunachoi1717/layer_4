package com.ventalize.shop.service;

import com.ventalize.shop.dto.cart.CartRead;
import com.ventalize.shop.entity.Cart;
import com.ventalize.shop.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService{

    private final CartRepository cartRepository;

    // 장바구니 상품 데이터 목록 조회(특정 회원)
    public List<CartRead> findAll(Integer memberId) {
        //리스트 값들을 DTO로 변환해서 리턴
        return cartRepository.findAllByMemberId(memberId).stream().map(Cart::toRead).toList();
    }

    // 장바구니 상품 데이터 조회(특정 회원의 특정 상품)
    public CartRead find(Integer memberId, Integer itemId) {
        Optional<Cart> cartOptional = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        // 값이 있으면 DTO로 변환해서 리턴, 없으면 null 리턴
        return cartOptional.map(Cart::toRead).orElse(null);
    }

    @Transactional
    public void removeAll(Integer memberId) {
        cartRepository.deleteByMemberId(memberId);

    }

    // 장바구니 상품 데이터 삭제(특정 회원의 특정 상품)
    @Transactional
    public void remove(Integer memberId, Integer itemId) {
        cartRepository.deleteByMemberIdAndItemId(memberId, itemId);

    }

    // 장바구니 데이터 저장(특정 회원의 특정 상품)
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    // 수량 1 증가
    @Transactional
    public void incrementQty(Integer memberId, Integer itemId) {
        addQty(memberId, itemId, 1);
    }

    // 수량 N 추가
    @Transactional
    public void addQty(Integer memberId, Integer itemId, int amount) {
        cartRepository.findByMemberIdAndItemId(memberId, itemId).ifPresent(cart -> {
            int current = Objects.requireNonNullElse(cart.getQuantity(), 1);
            cart.setQuantity(current + amount);
            cartRepository.save(cart);
        });
    }

    // 수량 직접 지정
    @Transactional
    public void updateQty(Integer memberId, Integer itemId, Integer qty) {
        cartRepository.findByMemberIdAndItemId(memberId, itemId).ifPresent(cart -> {
            cart.setQuantity(qty);
            cartRepository.save(cart);
        });
    }
}
