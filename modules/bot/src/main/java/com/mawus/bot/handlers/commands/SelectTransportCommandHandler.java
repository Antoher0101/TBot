package com.mawus.bot.handlers.commands;

import com.mawus.bot.exceptions.TransportTypeNotFound;
import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.TransportType;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.TransportService;
import com.mawus.core.service.TripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component("bot_SelectTransportCommandHandler")
public class SelectTransportCommandHandler implements CommandHandler, ActionHandler {
    protected static final String SELECT_TRANSPORT_ACTION = "addTrip:select-transport";

    protected final ClientActionRepository clientActionRepository;
    protected final TransportService transportService;
    protected final TripService tripService;
    protected final ClientTripStateRepository clientTripStateRepository;
    protected final ClientCommandStateRepository clientCommandStateRepository;
    protected final CommandHandlerRegistry commandHandlerRegistry;
    protected final ClientService clientService;

    public SelectTransportCommandHandler(ClientActionRepository clientActionRepository,
                                         TransportService transportService,
                                         TripService tripService,
                                         ClientTripStateRepository clientTripStateRepository,
                                         ClientCommandStateRepository clientCommandStateRepository,
                                         CommandHandlerRegistry commandHandlerRegistry,
                                         ClientService clientService) {
        this.clientActionRepository = clientActionRepository;
        this.transportService = transportService;
        this.tripService = tripService;
        this.clientTripStateRepository = clientTripStateRepository;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientService = clientService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && SELECT_TRANSPORT_ACTION.equals(action);
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if ("Отмена".equals(text)) {
            sendCancelledMessage(absSender, chatId);
            return;
        }

        handleSelectTransportTypeAction(absSender, chatId, text);
        executeNextCommand(absSender, update, chatId);
    }

    private void sendCancelledMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Рейс отменен")
                .build();
        absSender.execute(message);
    }

    private void handleSelectTransportTypeAction(AbsSender absSender, Long chatId, String text) {
        TransportType selectedType = transportService.findByName(text);

        if (selectedType == null) {
            throw new TransportTypeNotFound(String.format("Transport type '%s' not found", text));
        }

        clientTripStateRepository.updateTripTransportType(chatId, selectedType);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.MOCK).executeCommand(absSender, update, chatId); // todo
    }

    @Override
    public Command getCommand() {
        return Command.SELECT_TRANSPORT;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), SELECT_TRANSPORT_ACTION));

        ClientTrip clientTrip = clientTripStateRepository.findTripByChatId(chatId);

        if (clientTrip == null) {
            return;
        }

        sendSelectTransportMessage(absSender, chatId, clientTrip);
    }

    private void sendSelectTransportMessage(AbsSender absSender, Long chatId, ClientTrip clientTrip) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите тип транспорта")
                .replyMarkup(buildReplyKeyboardMarkup())
                .build();
        absSender.execute(message);
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true).selective(true);

        List<String> transportTypes = transportService.findAllTypes().stream().map(TransportType::getName).toList();

        keyboardBuilder.keyboardRow(new KeyboardRow(
                transportTypes.stream().map(tt -> KeyboardButton.builder().text(tt).build()).toList()));
        return keyboardBuilder.build();
    }
}
