package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.RoleType;
import com.trvankiet.app.entity.Role;
import com.trvankiet.app.repository.RoleRepository;
import com.trvankiet.app.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public InitializingBean sendDatabase() {
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
        };
    }
}
