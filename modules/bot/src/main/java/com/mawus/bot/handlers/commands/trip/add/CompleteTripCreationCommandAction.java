package com.mawus.bot.handlers.commands.trip.add;

import com.mawus.bot.handlers.commands.base.AbstractTripAction;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.ClientTripService;
import com.mawus.core.service.TripService;
import com.mawus.core.service.impl.ClientServiceImpl;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component("bot_CompleteTripCreationCommandAction")
public class CompleteTripCreationCommandAction extends AbstractTripAction {

    protected static final String COMPLETE_TRIP_CREATION_ACTION = "addTrip:complete-trip-creation";

    protected final TripService tripService;
    private final ClientService clientService;

    public CompleteTripCreationCommandAction(ClientActionRepository clientActionRepository,
                                             ClientTripService clientTripService,
                                             ClientCommandStateRepository clientCommandStateRepository,
                                             CommandHandlerRegistry commandHandlerRegistry, TripService tripService, ClientServiceImpl clientService) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.tripService = tripService;
        this.clientService = clientService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && COMPLETE_TRIP_CREATION_ACTION.equals(action);
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (Button.CANCEL.getAlias().equals(text)) {
            finish(absSender, chatId);
            sendCancelledMessage(absSender, chatId);
        }
        if (Button.CONFIRM.getAlias().equals(text)) {
//            saveTrip(chatId);
            finish(absSender, chatId);
            sendConfirmMessage(absSender, chatId);
        }

    }

    private void saveTrip(Long chatId) {
    }

    private void sendConfirmMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("ок.")
                .build();
        absSender.execute(message);
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), COMPLETE_TRIP_CREATION_ACTION));

        confirmMessage(absSender, chatId);
    }

    private void confirmMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
//        ClientTrip clientTrip = clientTripStateRepository.findTripByChatId(chatId);
//        Trip trip = clientTrip.getTripQuery();
//
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId)
//                .text(String.format("<Тут как-то будет реализована интеграция с API и вылезет список рейсов>\nВаш рейс: \n%s\n%s-%s\n%s-%s", trip.getTransport().getTransportType().getName(),
//                        trip.getCityFrom(), trip.getCityTo(), trip.getDepartureTime(), trip.getArrivalTime()))
//                .replyMarkup(buildReplyKeyboard())
//                .build();
//        absSender.execute(message);
    }

    @Override
    public Command getCommand() {
        return Command.COMPLETE_TRIP_CREATION;
    }

    private ReplyKeyboardMarkup buildReplyKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(
                        Button.createButton(Button.CONFIRM.getAlias()),
                        Button.createButton(Button.CANCEL.getAlias())
                ))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }
}
