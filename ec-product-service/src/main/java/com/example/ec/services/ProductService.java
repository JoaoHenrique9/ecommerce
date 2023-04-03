package com.example.ec.services;

import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.dtos.product.ProductResponseDto;
import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.ProductRepository;
import com.example.ec.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository repository;

	public ProductModel findById(UUID id) {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Produto não encontrado"));
	}
	
	 public Page<ProductModel> findAll(Pageable pageable) {
	        return repository.findAll(pageable);
	    }
	
	public ProductModel save(ProductModel entity) {
		return repository.save(entity);
	}
	
	public void delete(UUID id) {
		ProductModel entity = findById(id);
		repository.delete(entity);
	}
	
	public ProductModel update(ProductModel entity) {
		ProductModel newEntity =  findById(entity.getId());
		buildProductModel(entity, newEntity);
		return repository.save(newEntity);
	}

	public ProductModel buildProductModel(ProductRequestDto dto) {
		return ObjectMapperUtils.map(dto, ProductModel.class);
	}
	
	public ProductResponseDto  buildProductResponseDto(ProductModel  entity) {
		return ObjectMapperUtils.map(entity, ProductResponseDto.class);
	}
	
	public void buildProductModel(ProductModel entity, ProductModel newEntity) {
		ObjectMapperUtils.map(entity, newEntity);
	}

}
