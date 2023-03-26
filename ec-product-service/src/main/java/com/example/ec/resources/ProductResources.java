package com.example.ec.resources;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.ProductModel;
import com.example.ec.services.ProductService;

@RestController
@RequestMapping("product")
public class ProductResources {

	@Autowired
	ProductService service;

	@GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable UUID id) {
		ProductResponseDto dto = new ProductResponseDto(service.findById(id));
		return ResponseEntity.ok().body(dto);
	}

	@GetMapping
	public ResponseEntity<?> finAll(
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<ProductResponseDto> dtos = service.findAll(pageable).map(ProductResponseDto::new);
		return ResponseEntity.ok().body(dtos);
	}

	@PostMapping
	public ResponseEntity<?> save(@RequestBody @Valid ProductRequestDto dto) {
		ProductModel entity = service.buildProductModel(dto);
		service.save(entity);
		var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid ProductRequestDto dto) {
		ProductModel entity = service.buildProductModel(dto);
		entity.setId(id);
		service.update(entity);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}

}
