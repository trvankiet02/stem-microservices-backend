package com.trvankiet.app;

import com.trvankiet.app.entity.PostType;
import com.trvankiet.app.repository.PostTypeRepository;
import com.trvankiet.app.repository.ReactionTypeRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info =
    @Info(title = "Post API", version = "1.0", description = "Documentation Post API v1.0")
)
public class PostServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostServiceApplication.class, args);
    }

    @Autowired
    private PostTypeRepository postTypeRepository;
    @Autowired
    private ReactionTypeRepository reactionTypeRepository;
    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            if (postTypeRepository.findByPostTypeName("ASK").isEmpty())
                postTypeRepository.save(PostType.builder()
                        .postTypeId("1")
                        .postTypeName("ASK")
                        .postTypeDescription("Ask a question")
                        .createdAt(new Date())
                        .build());
            if (reactionTypeRepository.findByReactionTypeName("LIKE").isEmpty())
                reactionTypeRepository.save(com.trvankiet.app.entity.ReactionType.builder()
                        .reactionTypeId("1")
                        .reactionTypeName("LIKE")
                        .reactionTypeDescription("Like a post")
                        .createdAt(new Date())
                        .build());
        };
    }
}
