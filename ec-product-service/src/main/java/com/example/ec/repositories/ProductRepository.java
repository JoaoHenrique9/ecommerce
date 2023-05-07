package com.example.ec.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ec.models.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

    @Query("SELECT DISTINCT p FROM ProductModel p INNER JOIN p.categories c WHERE c.id = :categoryId")
    Page<ProductModel> findByCategoryId(Pageable pageable, UUID categoryId);

}
