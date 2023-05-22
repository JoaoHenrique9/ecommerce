package com.example.ec.resources;

import static com.example.ec.models.enums.OrderStatus.WAITING_PAYMENT;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.services.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderResource {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        var entity = orderService.buildToOrderModel(orderRequestDto);
        entity.setOrderStatus(WAITING_PAYMENT);
        orderService.insert(entity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
