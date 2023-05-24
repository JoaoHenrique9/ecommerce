package com.example.ec.dtos.order;

import java.util.List;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private UserDto user;

    private List<ProductDto> products;
}
