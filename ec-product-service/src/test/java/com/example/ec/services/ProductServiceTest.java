package com.example.ec.services;

import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final UUID RANDOM_UUID = UUID.randomUUID();
    private static final String JOHN_WICK = "John Wick";

    private ProductService productService;

    @Mock
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        productService = new ProductService(repository);
    }

    @Test
    void shouldFindById() {
        var id = RANDOM_UUID;
        var expectedProductModel = createProductModel();

        when(repository.findById(id)).thenReturn(Optional.of(expectedProductModel));

        var actualProductModel = productService.findById(id);

        assertThat(actualProductModel).usingRecursiveComparison().isEqualTo(expectedProductModel);
    }

    @Test
    void shouldThrowObjectNotFoundExceptionWhenFindById() {
        var id = RANDOM_UUID;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(id))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Produto não encontrado");

    }

    private static ProductModel createProductModel() {
        var productModel = new ProductModel();
        productModel.setId(RANDOM_UUID);
        productModel.setName(JOHN_WICK);
        return productModel;
    }
}