package com.example.ec.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.ec.dtos.category.CategoryIdRequestDto;
import com.example.ec.dtos.category.CategoryResponseDto;
import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.CategoryModel;
import com.example.ec.models.ProductModel;
import com.example.ec.services.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductResourcesTest {

	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String JOHN_WICK = "John Wick";
	private static final String PRODUCT_RESOURCE_PATH = "/products/";
	private static final String LOCALHOST = "http://localhost";

	@InjectMocks
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

		verify(productService).buildProductResponseDto(productModel);
		verify(productService).findById(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedProductResponseDto);
	}

	@Test
	void shouldFindAll() {
		var productModel = createProductModel();
		var productModelPageableList = new PageImpl<>(Arrays.asList(productModel));
		var pageable = createPageable();
		var expectedProductResponseDtoList = new PageImpl<>(Arrays.asList(createProductResponseDto()));

		when(productService.findAll(pageable)).thenReturn(productModelPageableList);
		when(productService.buildProductResponseDto(productModel)).thenCallRealMethod();

		var response = productResources.findAll(pageable);

		verify(productService).buildProductResponseDto(productModel);
		verify(productService).findAll(pageable);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedProductResponseDtoList);
	}

	@Test
	void shouldSave() {
		var productModel = createProductModel();
		var productRequestDto = createProductRequesteDto();
		var request = new MockHttpServletRequest("POST", "/products/");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		var expectedUri = URI.create(LOCALHOST + PRODUCT_RESOURCE_PATH + RANDOM_UUID);

		when(productService.buildProductModel(productRequestDto)).thenReturn(productModel);
		when(productService.save(productModel)).thenReturn(productModel);

		var response = productResources.save(productRequestDto);

		verify(productService).buildProductModel(productRequestDto);
		verify(productService).save(productModel);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
	}

	@Test
	void shouldUpdate() {
		var id = RANDOM_UUID;
		var model = createProductModelWithoutId();
		var RequestDto = createProductRequesteDto();

		when(productService.buildProductModel(RequestDto)).thenReturn(model);
		when(productService.update(model)).thenReturn(model);

		var response = productResources.update(id, RequestDto);

		verify(productService).buildProductModel(RequestDto);
		verify(productService)
				.update(Mockito.assertArg((modelToUpdate) -> assertThat(modelToUpdate.getId()).isEqualTo(id)));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

	@Test
	void shouldDelete() {
		var id = RANDOM_UUID;

		doNothing().when(productService).delete(id);

		var response = productResources.delete(id);

		verify(productService).delete(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

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
				.categories(Arrays.asList(createCategoryModel()))
				.build();
	}

	private static ProductModel createProductModelWithoutId() {
		var productModel = new ProductModel();
		productModel.setName(JOHN_WICK);
		return productModel;
	}

	private static ProductResponseDto createProductResponseDto() {
		return ProductResponseDto.builder()
				.id(RANDOM_UUID)
				.name(JOHN_WICK)
				.description(
						"A Smart TV LG 55 Polegadas oferece uma experiência imersiva com sua tela de alta resolução e recursos inteligentes.")
				.price(2900d)
				.quantity(10L)
				.isEnabled(true)
				.categories(Arrays.asList(createCategoryResponseDto()))
				.build();
	}

	private static ProductRequestDto createProductRequesteDto() {
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

	private static CategoryResponseDto createCategoryResponseDto() {
		return new CategoryResponseDto(RANDOM_UUID, "Eletrônico");
	}

	private static CategoryModel createCategoryModel() {
		return CategoryModel.builder()
				.id(RANDOM_UUID)
				.name("Eletrônico")
				.createdAt(new Date())
				.updatedAt(new Date())
				.build();
	}
}