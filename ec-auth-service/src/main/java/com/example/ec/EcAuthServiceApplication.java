package com.example.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
// @EnableFeignClients
@SpringBootApplication
public class EcAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcAuthServiceApplication.class, args);
	}

}
