package com.example.ec.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.models.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
@Builder
public class OrderModel {

    @Id
    private String id;
    private OrderStatus orderStatus;
    private UserDto user;
    private List<ProductDto> products;
    private Date createdAt;

    public Double getTotalPrice() {
        return products.stream()
                .mapToDouble(ProductDto::getTotalprice)
                .sum();
    }

}
