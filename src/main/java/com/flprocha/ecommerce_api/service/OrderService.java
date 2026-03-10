package com.flprocha.ecommerce_api.service;

import com.flprocha.ecommerce_api.dto.request.OrderRequest;
import com.flprocha.ecommerce_api.dto.response.OrderItemResponse;
import com.flprocha.ecommerce_api.dto.response.OrderResponse;
import com.flprocha.ecommerce_api.entity.*;
import com.flprocha.ecommerce_api.exception.ResourceNotFoundException;
import com.flprocha.ecommerce_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderResponse createFromCart(OrderRequest request) {
        Cart cart = cartRepository.findByCustomerEmail(request.getCustomerEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Carrinho não encontrado para: " + request.getCustomerEmail()));

        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .toList();

        if (cartItems.isEmpty())
            throw new RuntimeException("Carrinho vazio!");

        // Valida e baixa estoque de cada produto
        cartItems.forEach(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity())
                throw new RuntimeException(
                        "Estoque insuficiente para: " + product.getName());
            product.setStockQuantity(
                    product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        });

        // Cria o pedido
        Order order = Order.builder()
                .customerEmail(request.getCustomerEmail())
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .totalAmount(cart.getTotalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Cria os itens do pedido
        List<OrderItem> orderItems = new ArrayList<>(cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(savedOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .subtotal(cartItem.getSubtotal())
                        .build())
                .toList());

        savedOrder.setItems(orderItems);

        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        // Limpa o carrinho
        cartItemRepository.deleteAll(cartItems);

        return toResponse(savedOrder);
    }

    public OrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido não encontrado: " + id));
    }

    public Page<OrderResponse> findByCustomer(String email, Pageable pageable) {
        return orderRepository.findByCustomerEmail(email, pageable)
                .map(this::toResponse);
    }

    public OrderResponse processPayment(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido não encontrado: " + id));

        if (order.getPaymentStatus() == PaymentStatus.PAID)
            throw new RuntimeException("Pedido já foi pago!");

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Pedido cancelado não pode ser pago!");

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return toResponse(order);
    }

    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido não encontrado: " + id));

        if (order.getStatus() == OrderStatus.DELIVERED)
            throw new RuntimeException("Pedido entregue não pode ser cancelado!");

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Pedido já está cancelado!");

        // Devolve estoque
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerEmail(order.getCustomerEmail())
                .items(items)
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}