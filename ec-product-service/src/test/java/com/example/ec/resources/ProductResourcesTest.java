package com.example.ec.resources;

import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.ProductModel;
import com.example.ec.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductResourcesTest {

    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String JOHN_WICK = "John Wick";

    private ProductResources productResources;

    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productResources = new ProductResources(productService);
    }

    @Test
    void shouldFindById() {
        var id = RANDOM_UUID;
        var productModel = createProductModel();
        var expectedProductResponseDto = createProductResponseDto();

        when(productService.findById(id)).thenReturn(productModel);
        when(productService.buildProductResponseDto(productModel)).thenCallRealMethod();

        var response = productResources.findById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedProductResponseDto);
    }

    private static ProductModel createProductModel() {
        var productModel = new ProductModel();
        productModel.setId(RANDOM_UUID);
        productModel.setName(JOHN_WICK);
        return productModel;
    }

    private static ProductResponseDto createProductResponseDto() {
        return ProductResponseDto.builder()
                .id(RANDOM_UUID)
                .name(JOHN_WICK)
                .build();
    }
}