package com.example.demoinitial.config;

import com.example.demoinitial.utils.HasLogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Marker configuration for the dev profile.
 * Data seeding is handled by DevDataInitializer + DevDataService.
 */
@Configuration
@Profile("dev")
public class DevConfiguration implements HasLogger {

    public DevConfiguration() {
        getLogger().info("Dev Configuration active");
    }

}
