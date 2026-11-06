package com.library.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("book-service", r -> r.path("/api/books/**")
                        .uri("lb://book-service"))
                .route("member-service", r -> r.path("/api/members/**")
                        .uri("lb://member-service"))
                .route("borrowing-service", r -> r.path("/api/borrowings/**")
                        .uri("lb://borrowing-service"))
                .route("reservation-service", r -> r.path("/api/reservations/**")
                        .uri("lb://reservation-service"))
                .route("fine-service", r -> r.path("/api/fines/**")
                        .uri("lb://fine-service"))
                .route("review-service", r -> r.path("/api/reviews/**")
                        .uri("lb://review-service"))
                .route("notification-service", r -> r.path("/api/notifications/**")
                        .uri("lb://notification-service"))
                .route("statistics-service", r -> r.path("/api/statistics/**")
                        .uri("lb://statistics-service"))
                .build();
    }
}