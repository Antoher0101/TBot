package com.mawus.bot;

import com.mawus.bot.config.BotConfig;
import com.mawus.bot.exceptions.HandlerNotFoundException;
import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("bot_telegramBotService")
public class TelegramBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final int reconnectPause;

    private final String botUsername;

    private final String botToken;

    protected Map<Command, UpdateHandler> updateHandlers;
    protected Map<Command, ActionHandler> actionHandlers;

    protected final ClientActionRepository clientActionRepository;

    public TelegramBot(BotConfig config, ClientActionRepository clientActionRepository) throws TelegramApiException {
        this.botUsername = config.getBotUsername();
        this.botToken = config.getBotToken();
        this.reconnectPause = config.getReconnectPause();
        this.clientActionRepository = clientActionRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.debug(update.toString());
            handle(update);
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

    private void handle(Update update) throws TelegramApiException {
        if (handleCommand(update)) {
            log.debug("The command {} was performed", update.hasMessage()
                    ? update.getMessage().getText()
                    : update.getCallbackQuery().getMessage());
            return;
        }
        if (handleAction(update)) {
            log.debug("The action {} was performed", update.getMessage().getText());
        }
    }

    private boolean handleCommand(Update update) throws TelegramApiException {
        List<UpdateHandler> handlers = updateHandlers.values().stream().filter(
                command -> command.canHandleUpdate(update)
        ).toList();

        if (handlers.size() > 1) {
            throw new HandlerNotFoundException("Found more than one command handler: " + handlers.size());
        }
        if (handlers.size() != 1) {
            return false;
        }

        handlers.get(0).handleUpdate(this, update);
        return true;
    }

    private boolean handleAction(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            return false;
        }

        ClientAction clientAction = clientActionRepository.findByChatId(update.getMessage().getChatId());
        if (clientAction == null) {
            return false;
        }

        ActionHandler actionHandler = actionHandlers.get(clientAction.getCommand());
        if (actionHandler == null) {
            throw new HandlerNotFoundException("Failed to find action handler");
        }

        actionHandler.handleAction(this, update, clientAction.getAction());
        return true;
    }

    public void setHandlers(List<UpdateHandler> updateHandlers, List<ActionHandler> actionHandlers) {
        if (updateHandlers != null) {
            this.updateHandlers = updateHandlers.stream().collect(Collectors.toMap(UpdateHandler::getCommand, Function.identity()));
        }
        if (actionHandlers != null) {
            this.actionHandlers = actionHandlers.stream().collect(Collectors.toMap(ActionHandler::getCommand, Function.identity()));
        }
    }
}
