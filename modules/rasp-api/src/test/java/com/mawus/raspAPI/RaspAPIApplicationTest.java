package com.mawus.raspAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mawus.core", "com.mawus.raspAPI"})
public class RaspAPIApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(RaspAPIApplicationTest.class, args);
    }
}
