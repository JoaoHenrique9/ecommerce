package com.example.ec.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ec.models.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

}
