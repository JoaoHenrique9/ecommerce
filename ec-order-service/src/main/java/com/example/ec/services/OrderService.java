package com.example.ec.services;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;

import jakarta.validation.Valid;

public interface OrderService {
    void insert(OrderModel order);

    void updateStatus(String orderId, OrderStatus newStatus);

    OrderModel findById(String orderId);

    OrderModel buildToOrderModel(@Valid OrderRequestDto orderRequestDto);
}
