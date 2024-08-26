package com.mawus.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@PropertySource("classpath:application-bot.properties")
public class BotApplication {
    private static Logger log = LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
        log.info("Bot module was started.");
    }
}
