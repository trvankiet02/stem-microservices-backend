package com.trvanket.app.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("example", r -> r
                .path("/example/**")
                .uri("http://example-service"))
            .route("another", r -> r
                .path("/another/**")
                .uri("http://another-service"))
            .route("login", r -> r
                    .path("/login/**")
                    .uri("http://localhost:3000"))
            .build();
    }
}
