package com.mawus.bot.handlers.commands.trip.add;

import com.mawus.bot.exceptions.TransportTypeNotFound;
import com.mawus.bot.handlers.commands.base.AbstractTripAction;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.TransportType;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientTripService;
import com.mawus.core.service.TransportService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

@Component("bot_SelectTransportCommandHandler")
public class SelectTransportCommandHandler extends AbstractTripAction {
    protected static final String SELECT_TRANSPORT_ACTION = "addTrip:select-transport";

    protected final TransportService transportService;

    public SelectTransportCommandHandler(ClientActionRepository clientActionRepository,
                                         ClientTripService clientTripService,
                                         TransportService transportService,
                                         ClientCommandStateRepository clientCommandStateRepository,
                                         CommandHandlerRegistry commandHandlerRegistry) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.transportService = transportService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && SELECT_TRANSPORT_ACTION.equals(action);
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (Button.CANCEL.getAlias().equals(text)) {
            finish(absSender, chatId);
            sendCancelledMessage(absSender, chatId);
            return;
        }

        handleSelectTransportTypeAction(absSender, chatId, text);
        executeNextCommand(absSender, update, chatId);
    }

    private void handleSelectTransportTypeAction(AbsSender absSender, Long chatId, String text) {
        TransportType selectedType = transportService.findByName(text);

        if (selectedType == null) {
            throw new TransportTypeNotFound(String.format("Transport type '%s' not found", text));
        }

        clientTripService.updateTripTransportType(chatId, selectedType.getCode());
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.ENTER_CITY_DEPARTUERE).executeCommand(absSender, update, chatId);
    }

    @Override
    public Command getCommand() {
        return Command.SELECT_TRANSPORT;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), SELECT_TRANSPORT_ACTION));

        sendSelectTransportMessage(absSender, chatId);
    }

    private void sendSelectTransportMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите тип транспорта")
                .replyMarkup(buildReplyKeyboardMarkup())
                .build();
        absSender.execute(message);
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true).selective(true).oneTimeKeyboard(true);

        List<String> transportTypes = transportService.findAllTypes().stream().map(TransportType::getName).toList();

        List<KeyboardRow> rows = transportTypes.stream()
                .map(tt -> new KeyboardRow(List.of(Button.createButton(tt))))
                .collect(Collectors.toList());

        rows.add(new KeyboardRow(List.of(Button.createButton(Button.CANCEL.getAlias()))));
        keyboardBuilder.keyboard(rows);
        return keyboardBuilder.build();
    }
}
