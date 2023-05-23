package com.example.ec.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ec.models.OrderModel;

public interface OrderRepository extends MongoRepository<OrderModel, String> {

    List<OrderModel> findByUserId(UUID userId);
}
