package com.mawus.bot.handlers.commands.trip;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component("bot_EnterDepartureDateAction")
public class EnterDepartureDateAction extends AbstractTripAction {

    protected static final String ENTER_DEPARTURE_DATE_ACTION = "addTrip:enter-departure-date";

    protected final ClientTripService clientTripService;

    public EnterDepartureDateAction(ClientActionRepository clientActionRepository,
                                    ClientTripService clientTripService,
                                    ClientCommandStateRepository clientCommandStateRepository,
                                    CommandHandlerRegistry commandHandlerRegistry) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.clientTripService = clientTripService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && ENTER_DEPARTURE_DATE_ACTION.equals(action);
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

        LocalDate tripDate = parseDate(text);
        clientTripService.updateTripDate(chatId, tripDate);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.SELECT_TRIP).executeCommand(absSender, update, chatId);
    }

    private LocalDate parseDate(String tripDateText) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_DATE,
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(tripDateText, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Не удалось распознать формат даты: " + tripDateText);
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_DEPARTURE_DATE;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_DEPARTURE_DATE_ACTION));

        sendEnterDepartureDateMessage(absSender, chatId);
    }

    private void sendEnterDepartureDateMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите дату отправления:")
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
