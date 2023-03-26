package com.example.ec.services;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ec.dtos.product.ProductRequestDto;
import com.example.ec.exception.ObjectNotFoundException;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	ModelMapper modelMapper;

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
		return modelMapper.map(dto, ProductModel.class);
	}
	
	public void buildProductModel(ProductModel entity, ProductModel newEntity) {
		modelMapper.map(entity, newEntity);
	}

}
