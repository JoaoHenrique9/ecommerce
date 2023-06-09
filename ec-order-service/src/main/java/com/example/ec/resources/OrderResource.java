package com.example.ec.resources;

import static com.example.ec.models.enums.OrderStatus.WAITING_PAYMENT;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.dtos.order.OrderResponseDto;
import com.example.ec.models.enums.OrderStatus;
import com.example.ec.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderResource {

    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        var entity = orderService.buildToOrderModel(orderRequestDto);
        entity.setOrderStatus(WAITING_PAYMENT);
        orderService.insert(entity);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> findById(@PathVariable @NotEmpty @Size(max = 24) String orderId) {
        return ResponseEntity.ok(new OrderResponseDto(orderService.findById(orderId)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<Void> updateStatus(@PathVariable String orderId, @PathVariable int status) {
        orderService.updateStatus(orderId, OrderStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> findByUserId(@PathVariable UUID userId) {
        var orders = orderService.findByUserId(userId).stream().map(OrderResponseDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
}
