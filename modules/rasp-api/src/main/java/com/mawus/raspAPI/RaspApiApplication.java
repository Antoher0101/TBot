package com.mawus.raspAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {"com.mawus.core", "com.mawus.raspAPI"})
@Profile("!test")
public class RaspApiApplication implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(RaspApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RaspApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("RaspAPI module joining the thread.");
        Thread.currentThread().join();
    }
}
