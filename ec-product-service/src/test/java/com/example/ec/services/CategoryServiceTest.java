package com.example.ec.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.ec.dtos.category.CategoryRequestDto;
import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.models.CategoryModel;
import com.example.ec.repositories.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	private static final UUID RANDOM_UUID = UUID.randomUUID();
	private static final String CATEGORY_NAME = "Eletrônico";
	private static final String CATEGORY_NAME_2 = "Esportes";

	@InjectMocks
	private CategoryService categoryService;

	@Mock
	private CategoryRepository repository;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(repository);
	}

	@Test
	@DisplayName("Find category by id")
	void shouldFindById() {
		var id = RANDOM_UUID;
		var expectedCategoryModel = createCategoryModel();
		when(repository.findById(id)).thenReturn(Optional.of(expectedCategoryModel));

		var actualCategoryModel = categoryService.findById(id);

		assertThat(actualCategoryModel).usingRecursiveComparison().isEqualTo(expectedCategoryModel);
	}

	@Test
	@DisplayName("Throw ObjectNotFoundException when category not found by id")
	void shouldThrowObjectNotFoundExceptionWhenFindById() {
		var id = RANDOM_UUID;
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> categoryService.findById(id))
				.isInstanceOf(ObjectNotFoundException.class)
				.hasMessageContaining("Categoria não encontrada");
	}

	@Test
	@DisplayName("Find all categories")
	void shouldFindAll() {
		var expectedCategoryModelPageableList = createCategoryModelPageableList();
		var pageable = createPageable();
		when(repository.findAll(pageable)).thenReturn(expectedCategoryModelPageableList);

		var actualCategoryModelPageableList = categoryService.findAll(pageable);

		verify(repository).findAll(pageable);
		assertThat(actualCategoryModelPageableList).usingRecursiveComparison()
				.isEqualTo(expectedCategoryModelPageableList);
	}

	@Test
	@DisplayName("Save a category")
	void shouldSaveNewCategory() {
		var categoryModel = createCategoryModel();
		var expectedCategoryModel = createCategoryModel();
		when(repository.save(categoryModel)).thenReturn(expectedCategoryModel);

		var actualCategoryModel = categoryService.save(categoryModel);

		verify(repository).save(categoryModel);
		assertThat(actualCategoryModel).usingRecursiveComparison().isEqualTo(expectedCategoryModel);
	}

	@Test
	@DisplayName("Delete a category")
	void shouldDeleteCategory() {
		var id = RANDOM_UUID;
		var categoryModel = createCategoryModel();
		when(repository.findById(id)).thenReturn(Optional.of(categoryModel));
		doNothing().when(repository).delete(categoryModel);

		categoryService.delete(id);

		verify(repository).delete(categoryModel);
	}

	@Test
	@DisplayName("Update a category")
	void shouldUpdateCategory() {
		var id = RANDOM_UUID;
		var categoryModel = createCategoryModel();
		var updatedCategoryModel = updatedCategoryModel();
		when(repository.findById(id)).thenReturn(Optional.of(categoryModel));
		when(repository.save(categoryModel)).thenReturn(categoryModel);

		var result = categoryService.update(updatedCategoryModel);

		verify(repository).findById(categoryModel.getId());
		verify(repository).save(categoryModel);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(categoryModel.getId());
		assertThat(result.getName()).isEqualTo(updatedCategoryModel.getName());
	}

	@Test
	@DisplayName("MapDto to CategoryModel")
	void shouldMapDtoToCategoryModel() {
		CategoryRequestDto dto = createCategoryRequestDto();

		CategoryModel result = categoryService.buildCategoryModel(dto);

		assertEquals(dto.getName(), result.getName());
	}

	private static CategoryRequestDto createCategoryRequestDto() {
		return CategoryRequestDto.builder().name(CATEGORY_NAME).build();
	}

	private static CategoryModel createCategoryModel() {
		return new CategoryModel(RANDOM_UUID, CATEGORY_NAME);
	}

	private static CategoryModel updatedCategoryModel() {
		return new CategoryModel(RANDOM_UUID, CATEGORY_NAME_2);
	}

	private static List<CategoryModel> createCategoryModelList() {
		return Arrays.asList(createCategoryModel());

	}

	private static Pageable createPageable() {
		int page = 0;
		int size = 10;
		Sort sort = Sort.by("createdAt").descending();
		return PageRequest.of(page, size, sort);
	}

	private Page<CategoryModel> createCategoryModelPageableList() {
		return new PageImpl<CategoryModel>(createCategoryModelList());
	}
}