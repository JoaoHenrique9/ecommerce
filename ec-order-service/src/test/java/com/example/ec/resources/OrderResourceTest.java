package com.example.ec.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.ec.dtos.ProductDto;
import com.example.ec.dtos.UserDto;
import com.example.ec.dtos.order.OrderRequestDto;
import com.example.ec.dtos.order.OrderResponseDto;
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
    void setup() {
        orderResource = new OrderResource(orderService);
    }

    @Test
    public void shouldInsertOrder() {

        OrderRequestDto orderRequestDto = new OrderRequestDto();
        OrderModel orderModel = createOrderModel();

        var request = new MockHttpServletRequest("POST", "/orders/");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var expectedUri = URI.create("http://localhost" + "/orders/" + "123456");

        when(orderService.buildToOrderModel(orderRequestDto)).thenReturn(orderModel);
        when(orderService.insert(orderModel)).thenReturn(orderModel);

        var response = orderResource.insert(orderRequestDto);

        verify(orderService).buildToOrderModel(orderRequestDto);
        verify(orderService).insert(orderModel);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
        assertEquals(OrderStatus.WAITING_PAYMENT, orderModel.getOrderStatus());
    }

    @Test
    public void shouldFindById() {

        String orderId = "123456";
        OrderModel orderModel = createOrderModel();
        OrderResponseDto expectedResponse = new OrderResponseDto(orderModel);

        when(orderService.findById(orderId)).thenReturn(orderModel);

        ResponseEntity<OrderResponseDto> response = orderResource.findById(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);

    }

    @Test
    public void shouldUpdateStatus() {

        String orderId = "123";
        int status = 2;

        ResponseEntity<Void> response = orderResource.updateStatus(orderId, status);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).updateStatus(orderId, OrderStatus.valueOf(status));
    }

    @Test
    public void shouldFindByUserId() {

        UUID userId = UUID.randomUUID();
        List<OrderModel> expectedOrders = Arrays.asList(createOrderModel());

        when(orderService.findByUserId(userId)).thenReturn(expectedOrders);

        ResponseEntity<List<OrderResponseDto>> responseEntity = orderResource.findByUserId(userId);

        verify(orderService).findByUserId(userId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        assertThat(responseEntity.getBody())
                .isNotNull()
                .isNotEqualTo("");
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

    private OrderModel createOrderModel() {
        return OrderModel.builder()
                .id("123456")
                .products(Arrays.asList(createProductDto()))
                .orderStatus(OrderStatus.WAITING_PAYMENT)
                .user(createUserDto())
                .build();
    }

}
