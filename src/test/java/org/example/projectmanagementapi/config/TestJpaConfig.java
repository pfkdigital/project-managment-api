package org.example.projectmanagementapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class TestJpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }
}