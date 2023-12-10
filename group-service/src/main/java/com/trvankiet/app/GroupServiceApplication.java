package com.trvankiet.app;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.GroupType;
import com.trvankiet.app.constant.StateType;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info =
@Info(title = "Group API", version = "1.0", description = "Documentation Group API v1.0")
)
@RequiredArgsConstructor
public class GroupServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }

}
