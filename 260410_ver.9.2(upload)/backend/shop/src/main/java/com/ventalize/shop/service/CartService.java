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

    public List<CartRead> findAll(Integer memberId) {

        return cartRepository.findAllByMemberId(memberId).stream().map(Cart::toRead).toList();
    }

    public CartRead find(Integer memberId, Integer itemId) {
        Optional<Cart> cartOptional = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        return cartOptional.map(Cart::toRead).orElse(null);
    }

    @Transactional
    public void removeAll(Integer memberId) {
        cartRepository.deleteByMemberId(memberId);

    }

    @Transactional
    public void remove(Integer memberId, Integer itemId) {
        cartRepository.deleteByMemberIdAndItemId(memberId, itemId);

    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    @Transactional
    public void incrementQty(Integer memberId, Integer itemId) {
        addQty(memberId, itemId, 1);
    }

    @Transactional
    public void addQty(Integer memberId, Integer itemId, int amount) {
        cartRepository.findByMemberIdAndItemId(memberId, itemId).ifPresent(cart -> {
            int current = Objects.requireNonNullElse(cart.getQuantity(), 1);
            cart.setQuantity(current + amount);
            cartRepository.save(cart);
        });
    }

    @Transactional
    public void updateQty(Integer memberId, Integer itemId, Integer qty) {
        cartRepository.findByMemberIdAndItemId(memberId, itemId).ifPresent(cart -> {
            cart.setQuantity(qty);
            cartRepository.save(cart);
        });
    }
}
