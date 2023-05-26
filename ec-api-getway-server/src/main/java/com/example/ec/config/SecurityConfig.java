package com.example.ec.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.ec.exceptions.UnauthorizedException;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

        @Value("${jwt.secret}")
        private String jwtSecret;

        private static final String[] PUBLIC = {
                        "/v3/api-docs/**",

                        "/user-service/v2/api-docs",
                        "/user-service/v3/api-docs",
                        "/user-service/v3/api-docs/**",
                        "/user-service/swagger-resources",
                        "/user-service/swagger-resources/**",
                        "/user-service/configuration/ui",
                        "/user-service/configuration/security",
                        "/user-service/swagger-ui/**",
                        "/user-service/webjars/**",
                        "/user-service/swagger-ui.html",
                        "/user-service/swagger-config",

                        "/order-service/v2/api-docs",
                        "/order-service/v3/api-docs",
                        "/order-service/v3/api-docs/**",
                        "/order-service/swagger-resources",
                        "/order-service/swagger-resources/**",
                        "/order-service/configuration/ui",
                        "/order-service/configuration/security",
                        "/order-service/swagger-ui/**",
                        "/order-service/webjars/**",
                        "/order-service/swagger-ui.html",

                        "/auth-service/v2/api-docs",
                        "/auth-service/v3/api-docs",
                        "/auth-service/v3/api-docs/**",
                        "/auth-service/swagger-resources",
                        "/auth-service/swagger-resources/**",
                        "/auth-service/configuration/ui",
                        "/auth-service/configuration/security",
                        "/auth-service/swagger-ui/**",
                        "/auth-service/webjars/**",
                        "/auth-service/swagger-ui.html",

                        "/product-service/v2/api-docs",
                        "/product-service/v3/api-docs",
                        "/product-service/v3/api-docs/**",
                        "/product-service/swagger-resources",
                        "/product-service/swagger-resources/**",
                        "/product-service/configuration/ui",
                        "/product-service/configuration/security",
                        "/product-service/swagger-ui/**",
                        "/product-service/webjars/**",
                        "/product-service/swagger-ui.html",

                        "/auth-service/login",
                        "/auth-service/refresh-token",
                        "/actuator/info" };

        private static final String[] PUBLIC_GET = {
                        "/product-service/products",
                        "/product-service/products/{id}",
                        "/product-service/categories",
                        "/product-service/categories/{id}",
                        "/product-service/categories/{id}/products"
        };
        private static final String[] USER_GET = {
                        "/order-service/orders/user/{userId}",
                        "/user-service/users/{id}" };

        private static final String[] USER_PUT = {
                        "/order-service/orders/user/{userId}",
                        "/user-service/users/{id}" };

        private static final String[] USER_DELETE = {
                        "/user-service/users" };

        private static final String[] ADMIN = {
                        "/product-service/**",
                        "/user-service/**",
                        "/order-service/orders/**" };

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .authorizeExchange(exchange -> exchange
                                                .pathMatchers("/user-service/users/email").denyAll()
                                                .pathMatchers(PUBLIC).permitAll()
                                                .pathMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                                                .pathMatchers(HttpMethod.POST, "/user-service/users").permitAll()
                                                .pathMatchers(HttpMethod.POST, "/order-service/orders")
                                                .hasAnyRole("USER", "ADMIN")
                                                .pathMatchers(HttpMethod.GET, USER_GET).hasAnyRole("USER", "ADMIN")
                                                .pathMatchers(HttpMethod.GET, USER_PUT).hasAnyRole("USER", "ADMIN")
                                                .pathMatchers(HttpMethod.GET, USER_DELETE).hasAnyRole("USER", "ADMIN")
                                                .pathMatchers(ADMIN).hasRole("ADMIN")
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(server -> server
                                                .jwt()
                                                .jwtDecoder(jwtDecoder())
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .csrf(csrf -> csrf.disable())
                                .build();
        }

        @Bean
        public ReactiveJwtDecoder jwtDecoder() {
                byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
                SecretKey key = Keys.hmacShaKeyFor(keyBytes);
                return NimbusReactiveJwtDecoder.withSecretKey(key).build();
        }

        private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
                return new ReactiveJwtAuthenticationConverterAdapter(converter);
        }

        private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
                return jwt -> {
                        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
                        return authorities;
                };
        }

        private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

                List<String> roles = jwt.getClaim("roles");
                if (roles == null)
                        throw new UnauthorizedException("Não autorizado");
                List<GrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                return authorities;
        }

}
