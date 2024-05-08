package com.trvankiet.app;

import com.trvankiet.app.constant.QuestionTypeEnum;
import com.trvankiet.app.entity.QuestionType;
import com.trvankiet.app.repository.QuestionTypeRepository;
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
    @Info(title = "Exam API", version = "1.0", description = "Documentation Exam API v1.0")
)
public class ExamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamServiceApplication.class, args);
    }

    @Autowired
    private QuestionTypeRepository questionTypeRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            Date now = new Date();
            if (questionTypeRepository.findByCode(QuestionTypeEnum.SINGLE_CHOICE.getCode()).isEmpty()) {
                questionTypeRepository.save(QuestionType.builder()
                        .id(QuestionTypeEnum.SINGLE_CHOICE.getCode())
                        .code(QuestionTypeEnum.SINGLE_CHOICE.getCode())
                        .name(QuestionTypeEnum.SINGLE_CHOICE.toString())
                        .description("Một câu trả lời đúng")
                        .createdAt(now)
                        .build()
                );
            }
            if (questionTypeRepository.findByCode(QuestionTypeEnum.MULTIPLE_CHOICE.getCode()).isEmpty()) {
                questionTypeRepository.save(QuestionType.builder()
                        .id(QuestionTypeEnum.MULTIPLE_CHOICE.getCode())
                        .code(QuestionTypeEnum.MULTIPLE_CHOICE.getCode())
                        .name(QuestionTypeEnum.MULTIPLE_CHOICE.toString())
                        .description("Nhiều câu trả lời đúng")
                        .createdAt(now)
                        .build()
                );
            }
            if (questionTypeRepository.findByCode(QuestionTypeEnum.TRUE_FALSE.getCode()).isEmpty()) {
                questionTypeRepository.save(QuestionType.builder()
                        .id(QuestionTypeEnum.TRUE_FALSE.getCode())
                        .code(QuestionTypeEnum.TRUE_FALSE.getCode())
                        .name(QuestionTypeEnum.TRUE_FALSE.toString())
                        .description("Đúng hoặc sai")
                        .createdAt(now)
                        .build()
                );
            }
            if (questionTypeRepository.findByCode(QuestionTypeEnum.ESSAY.getCode()).isEmpty()) {
                questionTypeRepository.save(QuestionType.builder()
                        .id(QuestionTypeEnum.ESSAY.getCode())
                        .code(QuestionTypeEnum.ESSAY.getCode())
                        .name(QuestionTypeEnum.ESSAY.toString())
                        .description("Tự luận")
                        .createdAt(now)
                        .build()
                );
            }
        };
    }

}
