package com.example.ec.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.ProductModel;
import com.example.ec.services.ProductService;

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
		
		verify(productService).buildProductResponseDto(productModel);
	    verify(productService).findById(id);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedProductResponseDto);
	}

	@Test
	void shouldFindAll() {
		var productModel = createProductModel();
		var productModelPageableList = createProductModelPageableList(productModel);
		var pageable = createPageable();
		var expectedProductResponseDtoList = createProductResponseDtoList();

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
		var request = new MockHttpServletRequest("POST","/product/");
	    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	    var expectedUri = URI.create("http://localhost/product/" + RANDOM_UUID);
		
		when(productService.buildProductModel(productRequestDto)).thenReturn(productModel);
		when(productService.save(productModel)).thenReturn(productModel);
		
		var response = productResources.save(productRequestDto);
	
		verify(productService).buildProductModel(productRequestDto);
	    verify(productService).save(productModel);
	    
	    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	    assertThat(response.getHeaders().getLocation()).isEqualTo(expectedUri);
	}
	

	private static ProductModel createProductModel() {
		var productModel = new ProductModel();
		productModel.setId(RANDOM_UUID);
		productModel.setName(JOHN_WICK);
		return productModel;
	}

	private static ProductResponseDto createProductResponseDto() {
		return ProductResponseDto.builder().id(RANDOM_UUID).name(JOHN_WICK).build();
	}
	
	private static ProductRequestDto createProductRequesteDto() {
		return ProductRequestDto.builder().name(JOHN_WICK).build();
	}

	private Page<ProductResponseDto> createProductResponseDtoList() {
		var productResponseDtoList = new ArrayList<ProductResponseDto>();
	    productResponseDtoList.add(createProductResponseDto());
	    return new PageImpl<>(productResponseDtoList);
	}
	
	private static List<ProductModel> createProductModelList(ProductModel model) {
		return Arrays.asList(model);

	}

	private static Pageable createPageable() {
		int page = 0;
		int size = 10;
		Sort sort = Sort.by("createdAt").descending();
		return PageRequest.of(page, size, sort);
	}
	
	private Page<ProductModel> createProductModelPageableList(ProductModel model) {
		return new PageImpl<>(createProductModelList(model));
	}
}