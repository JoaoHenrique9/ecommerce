package com.example.ec.resources;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ec.dtos.category.CategoryRequestDto;
import com.example.ec.dtos.category.CategoryResponseDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.CategoryModel;
import com.example.ec.services.CategoryService;
import com.example.ec.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("categories")
public class CategoryResources {

    private final CategoryService service;

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id) {
        CategoryResponseDto dto = service.buildCategoryResponseDto(service.findById(id));
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoryResponseDto> dtos = service.findAll(pageable).map(service::buildCategoryResponseDto);
        return ResponseEntity.ok().body(dtos);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid CategoryRequestDto dto) {
        CategoryModel entity = service.buildCategoryModel(dto);
        service.save(entity);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid CategoryRequestDto dto) {
        CategoryModel entity = service.buildCategoryModel(dto);
        entity.setId(id);
        service.update(entity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Page<ProductResponseDto>> findAllByCategory(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable UUID id) {
        var dtos = productService.findAllByCategory(pageable, id).map(productService::buildProductResponseDto);
        return ResponseEntity.ok().body(dtos);
    }

}
