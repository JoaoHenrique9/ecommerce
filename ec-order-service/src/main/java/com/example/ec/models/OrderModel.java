package com.example.ec.models;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.models.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class OrderModel {

    @Id
    private String id;
    private OrderStatus orderStatus;
    private UserDto user;
    private List<ProductDto> products;

    public Double getTotalPrice() {
        double totalPrice = products.stream()
                .mapToDouble(ProductDto::getPrice)
                .sum();

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return Double.parseDouble(decimalFormat.format(totalPrice));
    }

}
