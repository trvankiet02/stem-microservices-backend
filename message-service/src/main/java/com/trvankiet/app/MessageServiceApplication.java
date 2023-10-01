package com.trvankiet.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
    @Info(title = "Message API", version = "1.0", description = "Documentation Message API v1.0")
)
public class MessageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageServiceApplication.class, args);
    }
}
