package com.example.ec.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
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

import com.example.ec.dtos.category.CategoryIdRequestDto;
import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.exceptions.ObjectNotFoundException;
import com.example.ec.exceptions.QuantityException;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String JOHN_WICK = "John Wick";
	private static final String MORPHEUS = "Morpheus";

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

		assertNotNull(actualProductModel.getCreatedAt());
		assertEquals(actualProductModel.getCreatedAt(), expectedProductModel.getCreatedAt());
		assertNotEquals(actualProductModel.getCreatedAt(), "");

		assertNotNull(actualProductModel.getUpdatedAt());
		assertNotEquals(actualProductModel.getUpdatedAt(), "");
		assertThat(expectedProductModel.getUpdatedAt()).isEqualTo(actualProductModel.getUpdatedAt());

		assertThat(actualProductModel.getIsEnabled())
				.isNotNull()
				.isNotEqualTo("")
				.isEqualTo(expectedProductModel.getIsEnabled());
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
		var expectedProductModelPageableList = new PageImpl<ProductModel>(Arrays.asList(createProductModel()));
		var pageable = createPageable();
		when(repository.findAll(pageable)).thenReturn(expectedProductModelPageableList);

		var actualProductModelPageableList = productService.findAll(pageable);

		verify(repository).findAll(pageable);
		assertThat(actualProductModelPageableList).usingRecursiveComparison()
				.isEqualTo(expectedProductModelPageableList);
	}

	@Test
	void shouldFindAllProductsByCategory() {
		UUID categoryId = RANDOM_UUID;
		Pageable pageable = createPageable();
		List<ProductModel> productList = Arrays.asList(createProductModel());

		Page<ProductModel> expectedPage = new PageImpl<>(productList, pageable, productList.size());

		when(repository.findByCategoryId(pageable, categoryId)).thenReturn(expectedPage);

		Page<ProductModel> resultPage = productService.findAllByCategory(pageable, categoryId);

		assertThat(resultPage).isEqualTo(expectedPage);
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
		var updatedProductModel = updatedProductModel();
		when(repository.findById(id)).thenReturn(Optional.of(productModel));
		when(repository.save(productModel)).thenReturn(productModel);

		var result = productService.update(updatedProductModel);

		verify(repository).findById(productModel.getId());
		verify(repository).save(productModel);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(productModel.getId());
		assertThat(result.getName()).isEqualTo(updatedProductModel.getName());
	}

	@Test
	@DisplayName("MapDto to ProductModel")
	void shouldMapDtoToProductModel() {
		ProductRequestDto dto = createProductRequestDto();

		ProductModel result = productService.buildProductModel(dto);

		assertEquals(dto.getName(), result.getName());
		assertEquals(dto.getDescription(), result.getDescription());
		assertEquals(dto.getPrice(), result.getPrice());
	}

	@Test
	void shouldSubtractQuantity() {
		Long expectedQuantity = 5L;
		ProductModel expetedProductModel = createProductModel();

		when(repository.findById(expetedProductModel.getId())).thenReturn(Optional.of(expetedProductModel));

		productService.subtractQuantity(expetedProductModel.getId(), 5L);

		assertEquals(expectedQuantity, expetedProductModel.getQuantity().longValue());
	}

	@Test
	void shouldSubtractQuantityEqual() {
		Long expectedQuantity = 0L;
		ProductModel expetedProductModel = createProductModel();
		when(repository.findById(expetedProductModel.getId())).thenReturn(Optional.of(expetedProductModel));

		productService.subtractQuantity(expetedProductModel.getId(), 10L);

		assertEquals(expectedQuantity, expetedProductModel.getQuantity().longValue());
	}

	@Test
	void shouldSubtractQuantityQuantityException() {

		ProductModel mockEntity = createProductModel();

		when(repository.findById(mockEntity.getId())).thenReturn(Optional.of(mockEntity));

		assertThatThrownBy(() -> productService.subtractQuantity(mockEntity.getId(), 11L))
				.isInstanceOf(QuantityException.class)
				.hasMessageContaining("O valor é maior que a quantidade atual");

		;
	}

	private static ProductModel createProductModel() {
		return ProductModel.builder()
				.id(RANDOM_UUID)
				.name(JOHN_WICK)
				.description(
						"A Smart TV LG 55 Polegadas oferece uma experiência imersiva com sua tela de alta resolução e recursos inteligentes.")
				.price(2900d)
				.quantity(10L)
				.isEnabled(true)
				.createdAt(new Date())
				.updatedAt(new Date())
				.build();
	}

	private static ProductModel updatedProductModel() {
		return ProductModel.builder()
				.id(RANDOM_UUID)
				.name(MORPHEUS)
				.description(
						"A Smart TV LG 55 Polegadas oferece uma experiência imersiva com sua tela de alta resolução e recursos inteligentes.")
				.price(2900d)
				.quantity(10L)
				.isEnabled(true)
				.createdAt(new Date())
				.updatedAt(new Date())
				.build();
	}

	private static ProductRequestDto createProductRequestDto() {
		return ProductRequestDto.builder()
				.name(JOHN_WICK)
				.description(
						"A Smart TV LG 55 Polegadas oferece uma experiência imersiva com sua tela de alta resolução e recursos inteligentes.")
				.price(2900d)
				.quantity(10L)
				.isEnabled(true)
				.categories(Arrays.asList(
						createCategoryIdRequestDto()))
				.build();
	}

	private static Pageable createPageable() {
		int page = 0;
		int size = 10;
		Sort sort = Sort.by("createdAt").descending();
		return PageRequest.of(page, size, sort);
	}

	private static CategoryIdRequestDto createCategoryIdRequestDto() {
		return new CategoryIdRequestDto(RANDOM_UUID);
	}
}