package com.example.ec.dtos;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private UUID id;

    private String name;

    @NotNull
    @Min(value = 1, message = "Tem que possui pelo menos 1 produto")
    private Long quantity;

    @NotNull
    @Min(value = 1, message = "preço tem que ser maior que 0")
    private Double price;

    public Double getTotalprice() {
        return price * quantity;
    }
}
