package com.flprocha.ecommerce_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @Email(message = "Email inválido")
    @NotNull(message = "Email é obrigatório")
    private String customerEmail;

    private String shippingAddress;

    private String notes;
}