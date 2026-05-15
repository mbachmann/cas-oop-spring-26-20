package com.example.demoinitial.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class JpaAuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // Replace with SecurityContext lookup when authentication is enabled.
        return () -> Optional.of("system");
    }
}

