package com.mawus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@PropertySource("classpath:application-core.properties")
@EnableJpaRepositories(basePackages = "com.mawus.core.repository")
@Profile("!test")
public class CoreApplication implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(CoreApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Core module joining the thread.");
        Thread.currentThread().join();
    }
}