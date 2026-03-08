package com.flprocha.ecommerce_api.service;

import com.flprocha.ecommerce_api.dto.request.CartItemRequest;
import com.flprocha.ecommerce_api.dto.response.CartItemResponse;
import com.flprocha.ecommerce_api.dto.response.CartResponse;
import com.flprocha.ecommerce_api.entity.Cart;
import com.flprocha.ecommerce_api.entity.CartItem;
import com.flprocha.ecommerce_api.entity.Product;
import com.flprocha.ecommerce_api.exception.ResourceNotFoundException;
import com.flprocha.ecommerce_api.repository.CartItemRepository;
import com.flprocha.ecommerce_api.repository.CartRepository;
import com.flprocha.ecommerce_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse addItem(CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado: " + request.getProductId()));

        if (!product.isActive())
            throw new ResourceNotFoundException("Produto inativo: " + product.getName());

        if (product.getStockQuantity() < request.getQuantity())
            throw new RuntimeException("Estoque insuficiente para: " + product.getName());

        Cart cart = cartRepository.findByCustomerEmail(request.getCustomerEmail())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .customerEmail(request.getCustomerEmail())
                                .totalAmount(BigDecimal.ZERO)
                                .build()));

        // Se produto já está no carrinho, atualiza quantidade
        cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .ifPresentOrElse(
                        existingItem -> {
                            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                            existingItem.setSubtotal(
                                    product.getPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
                            cartItemRepository.save(existingItem);
                        },
                        () -> {
                            CartItem item = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .unitPrice(product.getPrice())
                                    .subtotal(product.getPrice()
                                            .multiply(BigDecimal.valueOf(request.getQuantity())))
                                    .build();
                            cartItemRepository.save(item);
                        }
                );

        return getCart(request.getCustomerEmail());
    }

    public CartResponse removeItem(String customerEmail, Long productId) {
        Cart cart = cartRepository.findByCustomerEmail(customerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Carrinho não encontrado para: " + customerEmail));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item não encontrado no carrinho"));

        cartItemRepository.delete(item);
        return getCart(customerEmail);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String customerEmail) {
        Cart cart = cartRepository.findByCustomerEmail(customerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Carrinho não encontrado para: " + customerEmail));

        List<CartItem> items = cartItemRepository.findAll().stream()
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .toList();

        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
        cartRepository.save(cart);

        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .customerEmail(cart.getCustomerEmail())
                .items(itemResponses)
                .totalAmount(total)
                .totalItems(items.size())
                .build();
    }

    public void clearCart(String customerEmail) {
        Cart cart = cartRepository.findByCustomerEmail(customerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Carrinho não encontrado para: " + customerEmail));
        cartItemRepository.deleteAll(
                cartItemRepository.findAll().stream()
                        .filter(i -> i.getCart().getId().equals(cart.getId()))
                        .toList());
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}