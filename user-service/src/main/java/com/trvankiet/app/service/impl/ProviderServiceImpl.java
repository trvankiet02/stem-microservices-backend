package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.ProviderType;
import com.trvankiet.app.constant.RoleType;
import com.trvankiet.app.entity.Provider;
import com.trvankiet.app.repository.ProviderRepository;
import com.trvankiet.app.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public InitializingBean sendDatabase() {
        return () -> {
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

}
