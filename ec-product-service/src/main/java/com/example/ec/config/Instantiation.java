package com.example.ec.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.example.ec.models.CategoryModel;
import com.example.ec.models.ProductModel;
import com.example.ec.repositories.CategoryRepository;
import com.example.ec.repositories.ProductRepository;

@Configuration
public class Instantiation implements CommandLineRunner {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Override
        public void run(String... args) throws Exception {

                // Limpar os dados existentes
                productRepository.deleteAll();
                categoryRepository.deleteAll();

                // Inclusão dos produtos e categorias a partir do seed

                // Categorias
                CategoryModel category1 = new CategoryModel(null, "Eletrônicos", null, null);
                CategoryModel category2 = new CategoryModel(null, "Livros", null, null);
                CategoryModel category3 = new CategoryModel(null, "Automóveis", null, null);
                CategoryModel category4 = new CategoryModel(null, "Brinquedos", null, null);
                CategoryModel category5 = new CategoryModel(null, "Alimentos", null, null);
                CategoryModel category6 = new CategoryModel(null, "Móveis", null, null);

                categoryRepository.saveAll(
                                Arrays.asList(category1, category2, category3, category4, category5, category6));

                // Produtos
                ProductModel product1 = ProductModel.builder()
                                .name("iPhone 13 Pro")
                                .description("Um smartphone premium com tecnologia avançada.")
                                .quantity(50L)
                                .price(9999.99D)
                                .isEnabled(true)
                                .build();

                ProductModel product2 = ProductModel.builder()
                                .name("Dom Quixote")
                                .description("Um clássico da literatura que conta a história de um cavaleiro errante.")
                                .quantity(100L)
                                .price(39.99D)
                                .isEnabled(true)
                                .build();

                ProductModel product3 = ProductModel.builder()
                                .name("Volkswagen Golf GTI")
                                .description("Um carro esportivo com desempenho excepcional.")
                                .quantity(10L)
                                .price(85000.00D)
                                .isEnabled(true)
                                .build();

                ProductModel product4 = ProductModel.builder()
                                .name("Barbie Dreamhouse")
                                .description("Uma casa de bonecas completa com vários acessórios.")
                                .quantity(200L)
                                .price(49.99D)
                                .isEnabled(true)
                                .build();

                ProductModel product5 = ProductModel.builder()
                                .name("Lindt Excellence Chocolate Amargo")
                                .description("Um chocolate premium de alta qualidade.")
                                .quantity(500L)
                                .price(9.99D)
                                .isEnabled(true)
                                .build();

                ProductModel product6 = ProductModel.builder()
                                .name("Sofá Chesterfield")
                                .description("Um sofá clássico e elegante com acabamento em couro.")
                                .quantity(20L)
                                .price(1999.99D)
                                .isEnabled(true)
                                .build();

                product1.setCategories(Arrays.asList(category1));
                product2.setCategories(Arrays.asList(category2));
                product3.setCategories(Arrays.asList(category3));
                product4.setCategories(Arrays.asList(category4));
                product5.setCategories(Arrays.asList(category5));
                product6.setCategories(Arrays.asList(category6));

                productRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5, product6));

        }
}
