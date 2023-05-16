package com.example.ec.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.ec.dtos.category.CategoryRequestDto;
import com.example.ec.dtos.category.CategoryResponseDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.CategoryModel;
import com.example.ec.models.ProductModel;
import com.example.ec.services.CategoryService;
import com.example.ec.services.ProductService;

@ExtendWith(MockitoExtension.class)
class CategoryResourcesTest {

	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String CATEGORY_NAME = "Eletrônico";
	private static final String PRODUCT_RESOURCE_PATH = "/categories/";
	private static final String LOCALHOST = "http://localhost";

	private CategoryResources categoryResources;

	@Mock
	private CategoryService categoryService;

	@Mock
	private ProductService productService;

	@BeforeEach
	void setUp() {
		categoryResources = new CategoryResources(categoryService, productService);

	}

	@Test
	void shouldFindById() {
		var id = RANDOM_UUID;
		var categoryModel = createCategoryModel();
		var expectedCategoryResponseDto = createCategoryResponseDto();

		when(categoryService.findById(id)).thenReturn(categoryModel);
		when(categoryService.buildCategoryResponseDto(categoryModel)).thenCallRealMethod();

		var response = categoryResources.findById(id);

		verify(categoryService).buildCategoryResponseDto(categoryModel);
		verify(categoryService).findById(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedCategoryResponseDto);
	}

	@Test
	void shouldFindAll() {
		var categoryModel = createCategoryModel();
		var categoryModelPageableList = new PageImpl<>(Arrays.asList(categoryModel));
		var pageable = createPageable();
		var expectedCategoryResponseDtoList = new PageImpl<>(Arrays.asList(createCategoryResponseDto()));

		when(categoryService.findAll(pageable)).thenReturn(categoryModelPageableList);
		when(categoryService.buildCategoryResponseDto(categoryModel)).thenCallRealMethod();

		var response = categoryResources.findAll(pageable);

		verify(categoryService).buildCategoryResponseDto(categoryModel);
		verify(categoryService).findAll(pageable);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedCategoryResponseDtoList);
	}

	@Test
	void shouldSave() {
		var categoryModel = createCategoryModel();
		var categoryRequestDto = createCategoryRequesteDto();
		var request = new MockHttpServletRequest("POST", "/categories/");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		var expectedUri = URI.create(LOCALHOST + PRODUCT_RESOURCE_PATH + RANDOM_UUID);

		when(categoryService.buildCategoryModel(categoryRequestDto)).thenReturn(categoryModel);
		when(categoryService.save(categoryModel)).thenReturn(categoryModel);

		var response = categoryResources.save(categoryRequestDto);

		verify(categoryService).buildCategoryModel(categoryRequestDto);
		verify(categoryService).save(categoryModel);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
	}

	@Test
	void shouldUpdate() {
		var id = RANDOM_UUID;
		var model = createCategoryModelWithoutId();
		var RequestDto = createCategoryRequesteDto();

		when(categoryService.buildCategoryModel(RequestDto)).thenReturn(model);
		when(categoryService.update(model)).thenReturn(model);

		var response = categoryResources.update(id, RequestDto);

		verify(categoryService).buildCategoryModel(RequestDto);
		verify(categoryService)
				.update(Mockito.assertArg((modelToUpdate) -> assertThat(modelToUpdate.getId()).isEqualTo(id)));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

	@Test
	void shouldDelete() {
		var id = RANDOM_UUID;

		doNothing().when(categoryService).delete(id);

		var response = categoryResources.delete(id);

		verify(categoryService).delete(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	public void shouldFindAllProductsByCategory() {

		UUID categoryId = RANDOM_UUID;
		Pageable pageable = createPageable();

		List<ProductModel> productList = Arrays.asList(createProductModel());

		Page<ProductModel> productPage = new PageImpl<>(Arrays.asList(createProductModel()));

		when(productService.findAllByCategory(pageable, categoryId)).thenReturn(productPage);

		List<ProductResponseDto> expectedDtos = productList.stream()
				.map(productService::buildProductResponseDto)
				.collect(Collectors.toList());

		ResponseEntity<Page<ProductResponseDto>> response = categoryResources.findAllByCategory(pageable, categoryId);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getContent()).isEqualTo(expectedDtos);

	}

	private static CategoryModel createCategoryModel() {
		return CategoryModel.builder()
				.id(RANDOM_UUID)
				.name(CATEGORY_NAME)
				.createdAt(new Date())
				.updatedAt(new Date())
				.build();
	}

	private static ProductModel createProductModel() {
		return ProductModel.builder()
				.id(RANDOM_UUID)
				.name("John Wick")
				.description(
						"A Smart TV LG 55 Polegadas oferece uma experiência imersiva com sua tela de alta resolução e recursos inteligentes.")
				.price(2900d)
				.quantity(10L)
				.isEnabled(true)
				.categories(Arrays.asList(createCategoryModel()))
				.build();
	}

	private static CategoryModel createCategoryModelWithoutId() {
		return CategoryModel.builder()
				.name(CATEGORY_NAME)
				.createdAt(new Date())
				.updatedAt(new Date())
				.build();
	}

	private static CategoryResponseDto createCategoryResponseDto() {
		return CategoryResponseDto.builder().id(RANDOM_UUID).name(CATEGORY_NAME).build();
	}

	private static CategoryRequestDto createCategoryRequesteDto() {
		return CategoryRequestDto.builder().name(CATEGORY_NAME).build();
	}

	private static Pageable createPageable() {
		int page = 0;
		int size = 10;
		Sort sort = Sort.by("createdAt").descending();
		return PageRequest.of(page, size, sort);
	}
}