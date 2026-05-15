package com.example.demoinitial.config;

import com.example.demoinitial.utils.HasLogger;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Triggers dev seed data after the application context is fully started.
 * Using ApplicationRunner guarantees the full Spring context (including JPA / schema) is ready
 * and DevDataService.createData() is called through the Spring AOP proxy,
 * so the @Transactional boundary on that method is honoured.
 */
@Component
@Profile("dev")
@NullMarked
public class DevDataInitializer implements ApplicationRunner, HasLogger {

    @Autowired
    DevDataService devDataService;

    @Override
    public void run(ApplicationArguments args) {
        getLogger().info("DevDataInitializer: seeding dev data …");
        devDataService.createData();
        getLogger().info("DevDataInitializer: seed complete.");
    }
}


