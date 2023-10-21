package com.trvankiet.app;

import com.trvankiet.app.constant.GroupRole;
import com.trvankiet.app.repository.GroupMemberRoleRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info =
    @Info(title = "Group API", version = "1.0", description = "Documentation Group API v1.0")
)
public class GroupServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }

    @Autowired
    private GroupMemberRoleRepository groupMemberRoleRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            if (groupMemberRoleRepository.findByRoleName(GroupRole.GROUP_ADMIN.toString()).isEmpty()) {
                groupMemberRoleRepository.save(com.trvankiet.app.entity.GroupMemberRole.builder()
                        .roleId("1")
                        .roleName(GroupRole.GROUP_ADMIN.toString())
                        .roleDescription("Admin of group")
                        .build());
            }
            if (groupMemberRoleRepository.findByRoleName(GroupRole.GROUP_MEMBER.toString()).isEmpty()) {
                groupMemberRoleRepository.save(com.trvankiet.app.entity.GroupMemberRole.builder()
                        .roleId("2")
                        .roleName(GroupRole.GROUP_MEMBER.toString())
                        .roleDescription("Member of group")
                        .build());
            }
        };
    }
}
