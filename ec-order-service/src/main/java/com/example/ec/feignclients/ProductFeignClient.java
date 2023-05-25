package com.example.ec.feignclients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ec.dtos.ProductDto;

@Component
@FeignClient(name = "product-service", path = "/products")
public interface ProductFeignClient {
    @GetMapping(value = "/{id}")
    ResponseEntity<ProductDto> findById(@PathVariable UUID id);

    @PutMapping(value = "/{id}/subtract-quantity")
    ResponseEntity<Void> removeQuantity(@PathVariable UUID id, @RequestParam Long quantity);
}
