package com.trvankiet.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info =
    @Info(title = "Conversation API", version = "1.0", description = "Documentation Conversation API v1.0")
)
public class ConversationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversationServiceApplication.class, args);
    }
}
