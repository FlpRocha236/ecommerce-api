package com.flprocha.ecommerce_api.controller;

import com.flprocha.ecommerce_api.dto.request.CartItemRequest;
import com.flprocha.ecommerce_api.dto.response.CartResponse;
import com.flprocha.ecommerce_api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public CartResponse addItem(@Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @GetMapping("/{email}")
    public CartResponse getCart(@PathVariable String email) {
        return cartService.getCart(email);
    }

    @DeleteMapping("/{email}/item/{productId}")
    public CartResponse removeItem(
            @PathVariable String email,
            @PathVariable Long productId) {
        return cartService.removeItem(email, productId);
    }

    @DeleteMapping("/{email}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable String email) {
        cartService.clearCart(email);
    }
}