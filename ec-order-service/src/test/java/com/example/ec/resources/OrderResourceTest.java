package com.example.ec.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;
import com.example.ec.services.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderResourceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderResource orderResource;

    @BeforeEach

    @Test
    public void shouldInsertOrder() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();

        OrderModel orderModel = new OrderModel();
        when(orderService.buildToOrderModel(orderRequestDto)).thenReturn(orderModel);

        ResponseEntity<Void> responseEntity = orderResource.insert(orderRequestDto);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        verify(orderService, times(1)).insert(orderModel);

        assertEquals(OrderStatus.WAITING_PAYMENT, orderModel.getOrderStatus());
    }

}
