package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientTripService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.mawus.bot.model.Button.createGeneralMenuKeyboard;

public abstract class AbstractTripAction implements CommandHandler, ActionHandler {

    protected final ClientActionRepository clientActionRepository;
    protected final ClientCommandStateRepository clientCommandStateRepository;
    protected final CommandHandlerRegistry commandHandlerRegistry;
    protected final ClientTripService clientTripService;

    public AbstractTripAction(ClientActionRepository clientActionRepository, ClientCommandStateRepository clientCommandStateRepository, CommandHandlerRegistry commandHandlerRegistry, ClientTripService clientTripService) {
        this.clientActionRepository = clientActionRepository;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientTripService = clientTripService;
    }

    protected void sendCancelledMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Рейс отменен")
                .replyMarkup(createGeneralMenuKeyboard())
                .build();
        absSender.execute(message);
    }

    protected void finish(AbsSender absSender, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.deleteAllByChatId(chatId);
        clientActionRepository.deleteByChatId(chatId);
        clientTripService.disposeDraftTrip(chatId);

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Возврат к главному меню")
                .replyMarkup(Button.createGeneralMenuKeyboard())
                .build();
        absSender.execute(message);
    }
}
