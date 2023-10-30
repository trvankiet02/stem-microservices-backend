package com.trvanket.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final AuthFilter authFilter;
    private final String API_V1 = "/api/v1/";
    private final Map<String, List<String>> services = Map.of(
            "conversation-service", pathConfig(List.of("conversations")),
            "friend-service", pathConfig(List.of("friends")),
            "group-service", pathConfig(List.of("groups", "group-members", "events")),
            "media-service", pathConfig(List.of("files", "albums")),
            "message-service", pathConfig(List.of("messages")),
            "notification-service", pathConfig(List.of("notifications")),
            "post-service", pathConfig(List.of("posts", "comments", "likes")),
            "user-service", pathConfig(List.of("users", "credentials", "tokens", "auth"))
    );
    private List<String> pathConfig(List<String> paths) {
        return paths.stream()
                .map(path -> API_V1 + path + "/**")
                .collect(Collectors.toList());
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();
        for (Map.Entry<String, List<String>> entry : services.entrySet()) {
            String serviceName = entry.getKey();
            List<String> servicePath = entry.getValue();
            routes.route(serviceName, r -> r
                    .path("/v3/api-docs/" + serviceName)
                    .filters(f -> f.filter(authFilter.apply(new AuthFilterConfig())))
                    .uri("lb://" + serviceName)
            );
            for (String path : servicePath) {
                routes.route(serviceName, r -> r
                        .path(path)
                        .filters(f -> f.filter(authFilter.apply(new AuthFilterConfig())))
                        .uri("lb://" + serviceName)
                );
            }
        }
        return routes.build();
    }
}
