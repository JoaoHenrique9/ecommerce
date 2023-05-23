package com.example.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EcUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcUserServiceApplication.class, args);
	}

}
