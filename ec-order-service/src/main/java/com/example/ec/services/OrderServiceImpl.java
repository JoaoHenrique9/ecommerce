package com.example.ec.services;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.exceptions.ObjectNotFoundException;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;
import com.example.ec.repositories.OrderRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public void insert(OrderModel order) {
        orderRepository.save(order);
    }

    @Override
    public OrderModel findById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ObjectNotFoundException("Pedido não encontrado"));
    }

    @Override
    public OrderModel buildToOrderModel(@Valid OrderRequestDto orderRequestDto) {
        OrderModel order = new OrderModel();
        BeanUtils.copyProperties(orderRequestDto, order);
        return order;
    }

    @Override
    public void updateStatus(String orderId, OrderStatus newStatus) {
        OrderModel order = findById(orderId);
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
    }

}
