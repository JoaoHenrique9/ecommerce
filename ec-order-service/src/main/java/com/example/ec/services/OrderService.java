package com.example.ec.services;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.models.OrderModel;

import jakarta.validation.Valid;

public interface OrderService {
    void insert(OrderModel order);

    OrderModel buildToOrderModel(@Valid OrderRequestDto orderRequestDto);
}
