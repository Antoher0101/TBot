package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import com.mawus.core.service.ClientTripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component("bot_EnterArrivalCommandHandler")
public class EnterArrivalCommandHandler extends AbstractTripAction {

    protected static final String ENTER_ARRIVAL_ACTION = "addTrip:enter-arrival-city";

    protected final ClientTripService clientTripService;

    public EnterArrivalCommandHandler(ClientActionRepository clientActionRepository,
                                      ClientTripService clientTripService,
                                      ClientCommandStateRepository clientCommandStateRepository,
                                      CommandHandlerRegistry commandHandlerRegistry) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.clientTripService = clientTripService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && ENTER_ARRIVAL_ACTION.equals(action);
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

        handleEnterArrivalAction(absSender, chatId, text);
        executeNextCommand(absSender, update, chatId);
    }

    private void handleEnterArrivalAction(AbsSender absSender, Long chatId, String text) {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);

        if (clientTrip == null) {
            return;
        }

        clientTripService.updateCityArrival(chatId, text);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.ENTER_DEPARTURE_DATE).executeCommand(absSender, update, chatId);
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_ARRIVAL_ACTION));

        sendEnterArrivalMessage(absSender, chatId);
    }

    private void sendEnterArrivalMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите город назначения:")
                .replyMarkup(buildReplyKeyboard())
                .build();
        absSender.execute(message);
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_CITY_ARRIVAL;
    }

    private ReplyKeyboardMarkup buildReplyKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(Button.createButton(Button.CANCEL.getAlias())))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }
}
