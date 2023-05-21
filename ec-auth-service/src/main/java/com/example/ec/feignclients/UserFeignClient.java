// package com.example.ec.feignclients;

// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.example.ec.models.User;

// @Component
// @FeignClient(name = "ec-user", path = "/users")
// public interface UserFeignClient {
// @GetMapping(value = "/search")
// ResponseEntity<User> findByEmail(@RequestParam String email);
// }
