package com.example.ec.services;

import java.util.List;
import java.util.UUID;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;

import jakarta.validation.Valid;

public interface OrderService {
    void insert(OrderModel order);

    void updateStatus(String orderId, OrderStatus newStatus);

    OrderModel findById(String orderId);

    List<OrderModel> findByUserId(UUID userId);

    OrderModel buildToOrderModel(@Valid OrderRequestDto orderRequestDto);
}
