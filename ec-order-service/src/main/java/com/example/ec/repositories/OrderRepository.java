package com.example.ec.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.ec.models.OrderModel;

public interface OrderRepository extends MongoRepository<OrderModel, String> {

}
