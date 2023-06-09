package com.example.ec.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.exceptions.ObjectNotFoundException;
import com.example.ec.feignclients.ProductFeignClient;
import com.example.ec.feignclients.UserFeignClient;
import com.example.ec.models.OrderModel;
import com.example.ec.models.enums.OrderStatus;
import com.example.ec.repositories.OrderRepository;
import com.example.ec.security.config.JwtService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    private static final UUID RANDOM_UUID = UUID.randomUUID();

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private ProductFeignClient productFeignClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        orderService = new OrderServiceImpl(orderRepository, userFeignClient, productFeignClient, jwtService);
    }

    @Test
    public void shouldInsertOrder() {
        OrderModel orderModel = createOrderModel();
        UserDto userDto = createUserDto();
        ProductDto productDto = createProductDto();

        String token = jwtService.generateSystemToken();

        when(userFeignClient.findById(token, orderModel.getUser().getId())).thenReturn(ResponseEntity.ok(userDto));
        when(productFeignClient.findById(orderModel.getProducts().get(0).getId()))
                .thenReturn(ResponseEntity.ok(productDto));
        when(productFeignClient.removeQuantity(
                token, productDto.getId(), productDto.getQuantity()))
                .thenReturn(ResponseEntity.ok().build());

        OrderModel result = orderService.insert(orderModel);

        verify(userFeignClient).findById(token, orderModel.getUser().getId());
        verify(productFeignClient).findById(orderModel.getProducts().get(0).getId());
        verify(productFeignClient).removeQuantity(token, productDto.getId(), productDto.getQuantity());
        verify(orderRepository).save(orderModel);

        assertEquals(OrderStatus.WAITING_PAYMENT, orderModel.getOrderStatus());
        assertNotNull(result);
    }

    @Test
    public void shouldFindById() {
        String orderId = "123456";
        OrderModel order = createOrderModel();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderModel result = orderService.findById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void shouldThrowObjectNotFoundExceptionWhenFindById() {
        var id = "123456";
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(id))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    public void shouldBuildToOrderModel() {
        OrderRequestDto requestDto = createOrderRequestDto();

        OrderModel result = orderService.buildToOrderModel(requestDto);

        assertNotNull(result);
        assertEquals("Nome do usuario", result.getUser().getName());
    }

    @Test
    public void shouldUpdateStatus() {

        String orderId = "123456";
        OrderModel orderModel = createOrderModel();
        orderModel.setOrderStatus(OrderStatus.PAID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderModel));

        OrderStatus newStatus = OrderStatus.PAID;
        orderService.updateStatus(orderId, newStatus);

        verify(orderRepository).findById(orderId);
        assertEquals(newStatus, orderModel.getOrderStatus());
        verify(orderRepository).save(orderModel);
    }

    private ProductDto createProductDto() {
        return ProductDto.builder()
                .id(RANDOM_UUID)
                .name("Nome do produto")
                .price(50.0)
                .quantity(3L)
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(RANDOM_UUID)
                .name("Nome do usuario")
                .build();
    }

    private OrderRequestDto createOrderRequestDto() {
        return OrderRequestDto.builder()
                .products(Arrays.asList(createProductDto()))
                .user(createUserDto())
                .build();
    }

    private OrderModel createOrderModel() {
        return OrderModel.builder()
                .id("123456")
                .products(Arrays.asList(createProductDto()))
                .orderStatus(OrderStatus.WAITING_PAYMENT)
                .user(createUserDto())
                .build();
    }

}
