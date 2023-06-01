package com.example.ec.feignclients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.ec.dtos.UserDto;

@Component
@FeignClient(name = "user-service", path = "/users")
public interface UserFeignClient {

    @GetMapping(value = "/{id}")
    ResponseEntity<UserDto> findById(@RequestHeader("Authorization") String token, @PathVariable UUID id);
}
