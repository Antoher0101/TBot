package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractTripAction implements CommandHandler, ActionHandler {

    protected final ClientActionRepository clientActionRepository;
    protected final ClientCommandStateRepository clientCommandStateRepository;
    protected final CommandHandlerRegistry commandHandlerRegistry;

    public AbstractTripAction(ClientActionRepository clientActionRepository, ClientCommandStateRepository clientCommandStateRepository, CommandHandlerRegistry commandHandlerRegistry) {
        this.clientActionRepository = clientActionRepository;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.commandHandlerRegistry = commandHandlerRegistry;
    }

    protected void sendCancelledMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Рейс отменен")
                .build();
        absSender.execute(message);
    }

    protected void finish(Long chatId) {
        clientCommandStateRepository.deleteAllByChatId(chatId);
        clientActionRepository.deleteByChatId(chatId);
    }
}
