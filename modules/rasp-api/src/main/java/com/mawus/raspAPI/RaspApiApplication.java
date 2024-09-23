package com.mawus.raspAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.mawus.core"})
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
