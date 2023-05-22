package com.example.ec.dtos.order;

import java.util.List;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.models.OrderModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private String id;
    private String orderStatus;
    private UserDto user;
    private List<ProductDto> products;
    private Double totalPrice;

    public OrderResponseDto(OrderModel orderModel) {
        this.id = orderModel.getId();
        this.orderStatus = orderModel.getOrderStatus().toString();
        this.user = new UserDto(orderModel.getUser().getId(), orderModel.getUser().getName());
        this.products = orderModel.getProducts();
        this.totalPrice = orderModel.getTotalPrice();
    }
}
