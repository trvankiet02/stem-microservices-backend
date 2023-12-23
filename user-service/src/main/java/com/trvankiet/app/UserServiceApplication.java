package com.trvankiet.app;

import com.trvankiet.app.constant.ProviderType;
import com.trvankiet.app.constant.RoleType;
import com.trvankiet.app.entity.Provider;
import com.trvankiet.app.entity.Role;
import com.trvankiet.app.repository.ProviderRepository;
import com.trvankiet.app.repository.RoleRepository;
import com.trvankiet.app.service.ProviderService;
import com.trvankiet.app.service.RoleService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@OpenAPIDefinition(info =
    @Info(title = "User API", version = "1.0", description = "Documentation User API v1.0")
)
public class UserServiceApplication {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            if (roleRepository.findByCode(RoleType.ROLE_ADMIN.getCode()).isEmpty())
                roleRepository.save(Role.builder()
                        .code(RoleType.ROLE_ADMIN.getCode())
                        .name(RoleType.ROLE_ADMIN.toString())
                        .build());
            if (roleRepository.findByCode(RoleType.ROLE_USER.getCode()).isEmpty())
                roleRepository.save(Role.builder()
                        .code(RoleType.ROLE_USER.getCode())
                        .name(RoleType.ROLE_USER.toString())
                        .build());
            if (providerRepository.findByCode(ProviderType.PROVIDER_LOCAL.getCode()).isEmpty())
                providerRepository.save(Provider.builder()
                        .code(ProviderType.PROVIDER_LOCAL.getCode())
                        .name(ProviderType.PROVIDER_LOCAL.toString())
                        .build());
            if (providerRepository.findByCode(ProviderType.PROVIDER_FACEBOOK.getCode()).isEmpty())
                providerRepository.save(Provider.builder()
                        .code(ProviderType.PROVIDER_FACEBOOK.getCode())
                        .name(ProviderType.PROVIDER_FACEBOOK.toString())
                        .build());
            if (providerRepository.findByCode(ProviderType.PROVIDER_GOOGLE.getCode()).isEmpty())
                providerRepository.save(Provider.builder()
                        .code(ProviderType.PROVIDER_GOOGLE.getCode())
                        .name(ProviderType.PROVIDER_GOOGLE.toString())
                        .build());
        };
    }
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


}
