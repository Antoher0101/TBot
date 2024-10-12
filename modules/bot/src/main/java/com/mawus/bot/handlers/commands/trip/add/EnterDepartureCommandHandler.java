package com.mawus.bot.handlers.commands.trip.add;

import com.mawus.bot.handlers.commands.base.AbstractTripAction;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientTripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component("bot_EnterDepartureCommandHandler")
public class EnterDepartureCommandHandler extends AbstractTripAction {

    protected static final String ENTER_DEPARTURE_ACTION = "addTrip:enter_departure_city";

    private final ClientTripService clientTripService;

    public EnterDepartureCommandHandler(ClientCommandStateRepository clientCommandStateRepository,
                                        CommandHandlerRegistry commandHandlerRegistry,
                                        ClientActionRepository clientActionRepository,
                                        ClientTripService clientTripService) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.clientTripService = clientTripService;
    }


    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && ENTER_DEPARTURE_ACTION.equals(action);
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

        handleEnterDepartureAction(absSender, chatId, text);
        executeNextCommand(absSender, update, chatId);
    }

    private void handleEnterDepartureAction(AbsSender absSender, Long chatId, String text) {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);

        if (clientTrip == null) {
            return;
        }

        clientTripService.updateCityDeparture(chatId, text);
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_DEPARTURE_ACTION));
        sendEnterCityDepartureMessage(absSender, chatId);
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_CITY_DEPARTURE;
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.ENTER_CITY_ARRIVAL).executeCommand(absSender, update, chatId);
    }

    private void sendEnterCityDepartureMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите город отправления:")
                .replyMarkup(buildReplyKeyboard())
                .build();
        absSender.execute(message);
    }

    private ReplyKeyboardMarkup buildReplyKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(Button.createButton(Button.CANCEL.getAlias())))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }
}
