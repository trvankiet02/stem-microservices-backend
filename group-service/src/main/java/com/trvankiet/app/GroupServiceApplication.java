package com.trvankiet.app;

import com.trvankiet.app.constant.GroupAccessType;
import com.trvankiet.app.constant.GroupMemberRoleType;
import com.trvankiet.app.constant.GroupType;
import com.trvankiet.app.constant.StateType;
import com.trvankiet.app.repository.GroupConfigRepository;
import com.trvankiet.app.repository.GroupMemberRoleRepository;
import com.trvankiet.app.repository.StateRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GroupServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroupServiceApplication.class, args);
    }

    private final GroupMemberRoleRepository groupMemberRoleRepository;
    private final StateRepository stateRepository;
    private final GroupConfigRepository groupConfigRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            // GroupMemberRole
            if (groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_ADMIN.getCode()).isEmpty()) {
                groupMemberRoleRepository.save(com.trvankiet.app.entity.GroupMemberRole.builder()
                        .id(GroupMemberRoleType.GROUP_ADMIN.getCode())
                        .code(GroupMemberRoleType.GROUP_ADMIN.getCode())
                        .name(GroupMemberRoleType.GROUP_ADMIN.toString())
                        .description("Admin of group")
                        .build());
            }
            if (groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_MEMBER.getCode()).isEmpty()) {
                groupMemberRoleRepository.save(com.trvankiet.app.entity.GroupMemberRole.builder()
                        .id(GroupMemberRoleType.GROUP_MEMBER.getCode())
                        .code(GroupMemberRoleType.GROUP_MEMBER.getCode())
                        .name(GroupMemberRoleType.GROUP_MEMBER.toString())
                        .description("Member of group")
                        .build());
            }
            if (groupMemberRoleRepository.findByCode(GroupMemberRoleType.GROUP_OWNER.getCode()).isEmpty()) {
                groupMemberRoleRepository.save(com.trvankiet.app.entity.GroupMemberRole.builder()
                        .id(GroupMemberRoleType.GROUP_OWNER.getCode())
                        .code(GroupMemberRoleType.GROUP_OWNER.getCode())
                        .name(GroupMemberRoleType.GROUP_OWNER.toString())
                        .description("Owner of group")
                        .build());
            }
            // State
            if (stateRepository.findByCode(StateType.ACCEPTED.getCode()).isEmpty()) {
                stateRepository.save(com.trvankiet.app.entity.State.builder()
                        .id(StateType.ACCEPTED.getCode())
                        .code(StateType.ACCEPTED.getCode())
                        .name(StateType.ACCEPTED.toString())
                        .description("Accepted")
                        .build());
            }
            if (stateRepository.findByCode(StateType.PENDING.getCode()).isEmpty()) {
                stateRepository.save(com.trvankiet.app.entity.State.builder()
                        .id(StateType.PENDING.getCode())
                        .code(StateType.PENDING.getCode())
                        .name(StateType.PENDING.toString())
                        .description("Pending")
                        .build());
            }
            if (stateRepository.findByCode(StateType.REJECTED.getCode()).isEmpty()) {
                stateRepository.save(com.trvankiet.app.entity.State.builder()
                        .id(StateType.REJECTED.getCode())
                        .code(StateType.REJECTED.getCode())
                        .name(StateType.REJECTED.toString())
                        .description("Rejected")
                        .build());
            }
            // GroupConfig
            if (groupConfigRepository.findByCode("Tclass_Apublic_Mpublic").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tclass_Apublic_Mpublic")
                        .code("Tclass_Apublic_Mpublic")
                        .type(GroupType.CLASS.toString())
                        .accessibility(GroupAccessType.PUBLIC.toString())
                        .memberMode(GroupAccessType.PUBLIC.toString())
                        .description("Type: Class, Accessibility: Public, MemberMode: Public")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tclass_Apublic_Mprivate").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tclass_Apublic_Mprivate")
                        .code("Tclass_Apublic_Mprivate")
                        .type(GroupType.CLASS.toString())
                        .accessibility(GroupAccessType.PUBLIC.toString())
                        .memberMode(GroupAccessType.PRIVATE.toString())
                        .description("Type: Class, Accessibility: Public, MemberMode: Private")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tclass_Aprivate_Mpublic").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tclass_Aprivate_Mpublic")
                        .code("Tclass_Aprivate_Mpublic")
                        .type(GroupType.CLASS.toString())
                        .accessibility(GroupAccessType.PRIVATE.toString())
                        .memberMode(GroupAccessType.PUBLIC.toString())
                        .description("Type: Class, Accessibility: Private, MemberMode: Public")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tclass_Aprivate_Mprivate").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tclass_Aprivate_Mprivate")
                        .code("Tclass_Aprivate_Mprivate")
                        .type(GroupType.CLASS.toString())
                        .accessibility(GroupAccessType.PRIVATE.toString())
                        .memberMode(GroupAccessType.PRIVATE.toString())
                        .description("Type: Class, Accessibility: Private, MemberMode: Private")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tdiscussion_Apublic_Mpublic").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tdiscussion_Apublic_Mpublic")
                        .code("Tdiscussion_Apublic_Mpublic")
                        .type(GroupType.DISCUSSION.toString())
                        .accessibility(GroupAccessType.PUBLIC.toString())
                        .memberMode(GroupAccessType.PUBLIC.toString())
                        .description("Type: Discussion, Accessibility: Public, MemberMode: Public")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tdiscussion_Apublic_Mprivate").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tdiscussion_Apublic_Mprivate")
                        .code("Tdiscussion_Apublic_Mprivate")
                        .type(GroupType.DISCUSSION.toString())
                        .accessibility(GroupAccessType.PUBLIC.toString())
                        .memberMode(GroupAccessType.PRIVATE.toString())
                        .description("Type: Discussion, Accessibility: Public, MemberMode: Private")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tdiscussion_Aprivate_Mpublic").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tdiscussion_Aprivate_Mpublic")
                        .code("Tdiscussion_Aprivate_Mpublic")
                        .type(GroupType.DISCUSSION.getCode())
                        .accessibility(GroupAccessType.PRIVATE.getCode())
                        .memberMode(GroupAccessType.PUBLIC.getCode())
                        .description("Type: Discussion, Accessibility: Private, MemberMode: Public")
                        .build());
            }
            if (groupConfigRepository.findByCode("Tdiscussion_Aprivate_Mprivate").isEmpty()) {
                groupConfigRepository.save(com.trvankiet.app.entity.GroupConfig.builder()
                        .id("Tdiscussion_Aprivate_Mprivate")
                        .code("Tdiscussion_Aprivate_Mprivate")
                        .type(GroupType.DISCUSSION.toString())
                        .accessibility(GroupAccessType.PRIVATE.toString())
                        .memberMode(GroupAccessType.PRIVATE.toString())
                        .description("Type: Discussion, Accessibility: Private, MemberMode: Private")
                        .build());
            }
        };
    }
}
