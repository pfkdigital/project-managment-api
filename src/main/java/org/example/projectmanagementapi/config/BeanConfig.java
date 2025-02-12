package org.example.projectmanagementapi.config;

import org.example.projectmanagementapi.audit.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class BeanConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditAwareImpl();
    }
}
