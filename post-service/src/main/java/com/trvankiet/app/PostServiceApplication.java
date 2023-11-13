package com.trvankiet.app;

import com.trvankiet.app.constant.PostTypeEnum;
import com.trvankiet.app.entity.PostType;
import com.trvankiet.app.entity.ReactionType;
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
            // Post type
            if (postTypeRepository.findByCode(PostTypeEnum.POST.getCode()).isEmpty()) {
                postTypeRepository.save(PostType.builder()
                        .id(PostTypeEnum.POST.getCode())
                        .code(PostTypeEnum.POST.getCode())
                        .name(PostTypeEnum.POST.toString())
                        .createdAt(new Date())
                        .build()
                );
            }
            if (postTypeRepository.findByCode(PostTypeEnum.QUESTION.getCode()).isEmpty()) {
                postTypeRepository.save(PostType.builder()
                        .id(PostTypeEnum.QUESTION.getCode())
                        .code(PostTypeEnum.QUESTION.getCode())
                        .name(PostTypeEnum.QUESTION.toString())
                        .createdAt(new Date())
                        .build()
                );
            }
            if (postTypeRepository.findByCode(PostTypeEnum.DISCUSSION.getCode()).isEmpty()) {
                postTypeRepository.save(PostType.builder()
                        .id(PostTypeEnum.DISCUSSION.getCode())
                        .code(PostTypeEnum.DISCUSSION.getCode())
                        .name(PostTypeEnum.DISCUSSION.toString())
                        .createdAt(new Date())
                        .build()
                );
            }
            // Reaction type LIKE and DISLIKE
            if (reactionTypeRepository.findByCode("LIKE").isEmpty()) {
                reactionTypeRepository.save(ReactionType.builder()
                        .id("LIKE")
                        .code("LIKE")
                        .name("LIKE")
                        .createdAt(new Date())
                        .build()
                );
            }
            if (reactionTypeRepository.findByCode("DISLIKE").isEmpty()) {
                reactionTypeRepository.save(ReactionType.builder()
                        .id("DISLIKE")
                        .code("DISLIKE")
                        .name("DISLIKE")
                        .createdAt(new Date())
                        .build()
                );
            }
        };
    }
}
