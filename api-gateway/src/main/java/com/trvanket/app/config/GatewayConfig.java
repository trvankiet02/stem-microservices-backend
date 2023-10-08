package com.trvanket.app.config;

import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class GatewayConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000"); // Thay đổi * thành tên miền của ứng dụng frontend
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        // Tham số hóa RoutePredicateFactory với PathPatternParser
        return new CorsWebFilter(exchange -> corsConfig);

    }
}
