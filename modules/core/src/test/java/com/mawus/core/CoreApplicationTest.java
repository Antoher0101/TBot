package com.mawus.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@PropertySource("classpath:application-core-test.properties")
@EnableJpaRepositories(basePackages = "com.mawus.core.repository")
public class CoreApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplicationTest.class, args);
    }
}