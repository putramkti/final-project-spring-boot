package com.bootcamp19.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service-route", r -> r
                        .path("/auth/**")
                        .uri("lb://auth-service"))
                .route("product-service-route", r -> r
                        .path("/product/**")
                        .uri("lb://product-service"))
                .route("scheduler-service-route", r -> r
                        .path("/scheduler/**")
                        .uri("lb://scheduler-service"))
                .build();
    }
}
