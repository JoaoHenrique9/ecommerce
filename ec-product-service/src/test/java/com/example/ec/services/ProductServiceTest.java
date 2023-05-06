package com.example.ec.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String JOHN_WICK = "John Wick";

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository repository;

	@BeforeEach
	void setUp() {
		productService = new ProductService(repository);
	}

	@Test
	@DisplayName("Find product by id")
	void shouldFindById() {
		var id = RANDOM_UUID;
		var expectedProductModel = createProductModel();
		when(repository.findById(id)).thenReturn(Optional.of(expectedProductModel));

		var actualProductModel = productService.findById(id);

		assertThat(actualProductModel).usingRecursiveComparison().isEqualTo(expectedProductModel);
	}

	@Test
	@DisplayName("Throw ObjectNotFoundException when product not found by id")
	void shouldThrowObjectNotFoundExceptionWhenFindById() {
		var id = RANDOM_UUID;
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.findById(id))
				.isInstanceOf(ObjectNotFoundException.class)
				.hasMessageContaining("Produto não encontrado");
	}

	@Test
	@DisplayName("Find all products")
	void shouldFindAll() {
		var expectedProductModelPageableList = createProductModelPageableList();
		var pageable = createPageable();
		when(repository.findAll(pageable)).thenReturn(expectedProductModelPageableList);

		var actualProductModelPageableList = productService.findAll(pageable);

		verify(repository).findAll(pageable);
		assertThat(actualProductModelPageableList).usingRecursiveComparison()
				.isEqualTo(expectedProductModelPageableList);
	}

	@Test
	@DisplayName("Save a product")
	void shouldSaveNewProduct() {
		var productModel = createProductModel();
		var expectedProductModel = createProductModel();
		when(repository.save(productModel)).thenReturn(expectedProductModel);

		var actualProductModel = productService.save(productModel);

		verify(repository).save(productModel);
		assertThat(actualProductModel).usingRecursiveComparison().isEqualTo(expectedProductModel);
	}

	@Test
	@DisplayName("Delete a product")
	void shouldDeleteProduct() {
		var id = RANDOM_UUID;
		var productModel = createProductModel();
		when(repository.findById(id)).thenReturn(Optional.of(productModel));
		doNothing().when(repository).delete(productModel);

		productService.delete(id);

		verify(repository).delete(productModel);
	}

	@Test
	@DisplayName("Update a product")
	void shouldUpdateProduct() {
		var id = RANDOM_UUID;
		var productModel = createProductModel();
		var updatedProductModel = createProductModel();
		when(repository.findById(id)).thenReturn(Optional.of(productModel));
		when(repository.save(productModel)).thenReturn(productModel);

		var result = productService.update(updatedProductModel);

		verify(repository).findById(productModel.getId());
		verify(repository).save(productModel);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(productModel.getId());
		assertThat(result.getName()).isEqualTo(updatedProductModel.getName());
	}

	private static ProductModel createProductModel() {
		return new ProductModel(RANDOM_UUID, JOHN_WICK);
	}

	private static List<ProductModel> createProductModelList() {
		return Arrays.asList(createProductModel());

	}

	private static Pageable createPageable() {
		int page = 0;
		int size = 10;
		Sort sort = Sort.by("createdAt").descending();
		return PageRequest.of(page, size, sort);
	}

	private Page<ProductModel> createProductModelPageableList() {
		return new PageImpl<ProductModel>(createProductModelList());
	}
}