package com.mawus.bot;

import com.mawus.bot.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final int reconnectPause;

    private final String botUsername;

    private final String botToken;

    public TelegramBot(BotConfig config) {
        super(new DefaultBotOptions(), config.getBotToken());
        this.botUsername = config.getBotUsername();
        this.botToken = config.getBotToken();
        this.reconnectPause = config.getReconnectPause();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info(update.toString());
        } catch (Exception e) {
            log.error("Failed to handle update", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public BotSession connect() {
        BotSession session = null;
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            session = telegramBotsApi.registerBot(this);
            log.info("[STARTED] TelegramAPI. Bot connected. " + this);
        } catch (TelegramApiException e) {
            log.error("Can't Connect. Pause " + reconnectPause / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(reconnectPause);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return null;
            }
            connect();
        }
        return session;
    }
}
