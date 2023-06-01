package com.example.ec.resources;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.models.ProductModel;
import com.example.ec.services.ProductService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductResource {

	private final ProductService service;

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> findById(@PathVariable UUID id) {
		ProductResponseDto dto = service.buildProductResponseDto(service.findById(id));
		return ResponseEntity.ok().body(dto);
	}

	@GetMapping
	public ResponseEntity<Page<ProductResponseDto>> findAll(
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<ProductResponseDto> dtos = service.findAll(pageable).map(service::buildProductResponseDto);
		return ResponseEntity.ok().body(dtos);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<Void> save(@RequestBody @Valid ProductRequestDto dto) {
		ProductModel entity = service.buildProductModel(dto);
		service.save(entity);
		var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid ProductRequestDto dto) {
		ProductModel entity = service.buildProductModel(dto);
		entity.setId(id);
		service.update(entity);

		return ResponseEntity.noContent().build();
	}

	@Hidden
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/{id}/subtract-quantity")
	public ResponseEntity<Void> subtractQuantity(@PathVariable UUID id, @NotEmpty @RequestParam Long quantity) {
		service.subtractQuantity(id, quantity);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}

}
