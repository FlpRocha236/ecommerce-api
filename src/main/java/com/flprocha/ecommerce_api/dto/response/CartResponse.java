package com.flprocha.ecommerce_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private String customerEmail;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private int totalItems;
}