package com.flprocha.ecommerce_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @Email(message = "Email inválido")
    @NotNull(message = "Email é obrigatório")
    private String customerEmail;

    @NotNull(message = "Produto é obrigatório")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Quantidade mínima é 1")
    private Integer quantity;
}