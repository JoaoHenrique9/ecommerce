package com.example.ec.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.exceptions.ObjectNotFoundException;
import com.example.ec.exceptions.ProductException;
import com.example.ec.feignclients.ProductFeignClient;
import com.example.ec.feignclients.UserFeignClient;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;
import com.example.ec.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserFeignClient userFeignClient;

    private final ProductFeignClient productFeignClient;

    @Override
    public OrderModel insert(OrderModel order) {
        userIdentityChecker(order.getUser());
        order.getProducts().stream().forEach(p -> productIdentityChecker(p));
        order.setCreatedAt(new Date());
        orderRepository.save(order);
        try {
            order.getProducts().stream().forEach(p -> productFeignClient.removeQuantity(p.getId(), p.getQuantity()));
        } catch (feign.FeignException.NotFound ex) {
            throw new ObjectNotFoundException("O valor é maior que a quantidade atual");
        }
        return order;

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

    @Override
    public List<OrderModel> findByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    private UserDto userIdentityChecker(UserDto dto) {
        UserDto user;
        try {
            var response = userFeignClient.findById(dto.getId());
            user = response.getBody();
            if (!dto.equals(user))
                throw new ObjectNotFoundException("Usuario não encontrado");
        } catch (feign.FeignException.NotFound ex) {
            throw new ObjectNotFoundException("Usuario não encontrado");
        }
        return user;
    }

    private ProductDto productIdentityChecker(ProductDto dto) {
        ProductDto product;
        try {
            var response = productFeignClient.findById(dto.getId());
            product = response.getBody();
            if (!dto.equals(product))
                throw new ObjectNotFoundException("Produto não encontrado");
            else if (product.getQuantity() == 0)
                throw new ProductException("Produto sem estoque");
            else if ((dto.getQuantity() == 0))
                throw new ProductException("Quantidade invalida");
            else if (product.getQuantity() < dto.getQuantity())
                throw new ProductException("O produto não possui essa quantidade em estoque");
        } catch (feign.FeignException.NotFound ex) {
            throw new ObjectNotFoundException("Produto não encontrado");
        }
        return product;
    }

}
