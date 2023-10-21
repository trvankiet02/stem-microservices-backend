package com.trvankiet.app;

import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.entity.Role;
import com.trvankiet.app.repository.RoleRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.sql.Timestamp;
import java.util.Date;

@SpringBootApplication
@EnableAsync
@OpenAPIDefinition(info =
    @Info(title = "User API", version = "1.0", description = "Documentation User API v1.0")
)
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Autowired
    private RoleRepository roleRepository;
    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            if (roleRepository.findByRoleName(RoleBasedAuthority.ADMIN.toString()).isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleBasedAuthority.ADMIN.toString())
                        .build());
            }
            if (roleRepository.findByRoleName(RoleBasedAuthority.TEACHER.toString()).isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleBasedAuthority.TEACHER.toString())
                        .build());
            }
            if (roleRepository.findByRoleName(RoleBasedAuthority.STUDENT.toString()).isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleBasedAuthority.STUDENT.toString())
                        .build());
            }
            if (roleRepository.findByRoleName(RoleBasedAuthority.PARENT.toString()).isEmpty()) {
                roleRepository.save(Role.builder()
                        .roleName(RoleBasedAuthority.PARENT.toString())
                        .build());
            }
        };
    }

}
