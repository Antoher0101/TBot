package com.mawus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@PropertySource("classpath:application-core.properties")
@EnableJpaRepositories(basePackages = "com.mawus.core.repository")
public class CoreApplicationTest {
    private static Logger log = LoggerFactory.getLogger(CoreApplicationTest.class);

    public static void main(String[] args) {
        SpringApplication.run(CoreApplicationTest.class, args);
    }
}