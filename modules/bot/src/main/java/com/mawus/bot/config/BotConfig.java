package com.mawus.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.generics.TelegramBot;

@Configuration
public class BotConfig {
    @Value("${telegram.bot.name}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.reconnectPause}")
    private int reconnectPause;

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public int getReconnectPause() {
        return reconnectPause;
    }

    public void setReconnectPause(int reconnectPause) {
        this.reconnectPause = reconnectPause;
    }
}
