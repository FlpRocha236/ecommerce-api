package com.flprocha.ecommerce_api.controller;

import com.flprocha.ecommerce_api.dto.request.OrderRequest;
import com.flprocha.ecommerce_api.dto.response.OrderResponse;
import com.flprocha.ecommerce_api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest request) {
        return orderService.createFromCart(request);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping("/customer/{email}")
    public Page<OrderResponse> findByCustomer(
            @PathVariable String email,
            @PageableDefault(size = 10) Pageable pageable) {
        return orderService.findByCustomer(email, pageable);
    }

    @PatchMapping("/{id}/pay")
    public OrderResponse processPayment(@PathVariable Long id) {
        return orderService.processPayment(id);
    }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id) {
        return orderService.cancel(id);
    }
}