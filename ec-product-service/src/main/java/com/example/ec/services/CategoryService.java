package com.example.ec.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ec.dtos.category.CategoryRequestDto;
import com.example.ec.dtos.category.CategoryResponseDto;
import com.example.ec.exceptions.ObjectNotFoundException;
import com.example.ec.models.CategoryModel;
import com.example.ec.repositories.CategoryRepository;
import com.example.ec.utils.ObjectMapperUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryModel findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Categoria não encontrada"));
    }

    public Page<CategoryModel> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public CategoryModel save(CategoryModel entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        CategoryModel entity = findById(id);
        repository.delete(entity);
    }

    public CategoryModel update(CategoryModel entity) {
        CategoryModel newEntity = findById(entity.getId());
        buildCategoryModel(entity, newEntity);
        return repository.save(newEntity);
    }

    public CategoryModel buildCategoryModel(CategoryRequestDto dto) {
        return ObjectMapperUtils.map(dto, CategoryModel.class);
    }

    public CategoryResponseDto buildCategoryResponseDto(CategoryModel entity) {
        return ObjectMapperUtils.map(entity, CategoryResponseDto.class);
    }

    public void buildCategoryModel(CategoryModel entity, CategoryModel newEntity) {
        ObjectMapperUtils.map(entity, newEntity);
    }

}
