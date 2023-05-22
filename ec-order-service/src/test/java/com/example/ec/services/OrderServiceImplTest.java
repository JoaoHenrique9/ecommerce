package com.example.ec.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.models.OrderModel;
import com.example.ec.repositories.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    public void shouldInsert() {

        OrderModel order = new OrderModel();

        orderService.insert(order);

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void shouldBuildToOrderModel() {
        OrderRequestDto requestDto = createOrderRequestDto();

        OrderModel result = orderService.buildToOrderModel(requestDto);

        assertNotNull(result);
        assertEquals("Nome do usuario", result.getUser().getName());
    }

    public void testNullOrderModelThrowsException() {

        assertThatThrownBy(() -> orderService.insert(null))
                .isInstanceOf(
                        NullPointerException.class);
    }

    private ProductDto createProductDto() {
        return ProductDto.builder()
                .id(UUID.randomUUID())
                .name("Nome do produto")
                .price(50.0)
                .quantity(3L)
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(UUID.randomUUID())
                .name("Nome do usuario")
                .build();
    }

    private OrderRequestDto createOrderRequestDto() {
        return OrderRequestDto.builder()
                .products(Arrays.asList(createProductDto()))
                .user(createUserDto())
                .build();
    }

}
