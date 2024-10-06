package com.mawus.bot.handlers.commands.trip;

import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.commands.base.AbstractTripAction;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.app.AppContextProvider;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.domain.TripResponse;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientTripService;
import com.mawus.core.service.TripService;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import com.mawus.raspAPI.services.TripRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("bot_SelectTripCommandHandler")
public class SelectTripCommandHandler extends AbstractTripAction implements UpdateHandler {
    protected static final String SELECT_TRIP_ACTION = "selectTrip:select-trip";

    protected static final String NEXT_PAGE_CALLBACK = "selectTrip:next-page";
    protected static final String PREV_PAGE_CALLBACK = "selectTrip:prev-page";
    protected static final String SELECT_TRIP_CALLBACK = "selectTrip:select-trip";
    protected static final String CONFIRM_TRIP_CALLBACK = "selectTrip:confirm-trip";
    protected static final String CANCEL_TRIP_CALLBACK = "selectTrip:cancel-trip";

    private final ClientTripService clientTripService;
    private final TripRequestService tripRequestService;

    public SelectTripCommandHandler(ClientActionRepository clientActionRepository,
                                    ClientCommandStateRepository clientCommandStateRepository,
                                    CommandHandlerRegistry commandHandlerRegistry,
                                    ClientTripService clientTripService,
                                    TripRequestService tripRequestService) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.clientTripService = clientTripService;
        this.tripRequestService = tripRequestService;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return isCallbackQueryUpdate(update);
    }

    private boolean isCallbackQueryUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            return callbackData.equals(NEXT_PAGE_CALLBACK) ||
                    callbackData.equals(PREV_PAGE_CALLBACK) ||
                    callbackData.equals(CONFIRM_TRIP_CALLBACK) ||
                    callbackData.equals(CANCEL_TRIP_CALLBACK) ||
                    callbackData.startsWith(SELECT_TRIP_CALLBACK);
        }
        return false;
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        if (isCallbackQueryUpdate(update)) {
            handleCallbackQueryUpdate(absSender, update);
        }
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return isSelectTripAction(update, action);
    }

    private boolean isSelectTripAction(Update update, String action) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                SELECT_TRIP_ACTION.equals(action);
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
        finish(absSender, chatId);
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), SELECT_TRIP_ACTION));
        suggestNextTripsMessage(absSender, chatId, null);
    }

    @Override
    public Command getCommand() {
        return Command.SELECT_TRIP;
    }

    private void suggestNextTripsMessage(AbsSender absSender, Long chatId, Integer messageId) throws TelegramApiException {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);
        if (clientTrip == null) {
            finish(absSender, chatId);
            return;
        }
        int currentPage = clientTrip.getCurrentPage();

        TripResponse response;
        int tripsPerPage = 5;
        long offset = (currentPage - 1L) * tripsPerPage;
        try {
            response = tripRequestService.fetchNextStations(clientTrip.getTripQuery(), offset);
        } catch (ParserException | ValidationException | HTTPClientException e) {
            throw new RuntimeException(e);
        }
        Set<Trip> trips = response.getTrips();
        if (trips.isEmpty()) {
            SendMessage emptyMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text("К сожалению, подходящих рейсов не найдено.")
                    .build();
            absSender.execute(emptyMessage);
            finish(absSender, chatId);
            return;
        }

        clientTripService.updateAvailableTrips(chatId, new ArrayList<>(trips));
        clientTripService.updateTripOffset(chatId, offset);

        String text = formatTrips(trips, currentPage, tripsPerPage, response.getTotalResults());
        InlineKeyboardMarkup keyboardMarkup = buildPaginationKeyboard(currentPage, response.getTotalResults(), tripsPerPage);

        if (messageId != null) {
            EditMessageText editMessage = EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboardMarkup)
                    .build();
            absSender.execute(editMessage);
        } else {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .replyMarkup(keyboardMarkup)
                    .build();
            absSender.execute(message);
        }
    }

    public String formatTrips(Collection<Trip> trips, int page, int pageSize, Long totalTrips) {
        StringBuilder message = new StringBuilder();
        int startTripNumber = (page - 1) * pageSize + 1;

        message.append("Список доступных рейсов (стр. ").append(page).append(" из ")
                .append((int) Math.ceil((double) totalTrips / pageSize)).append("):\n\n");

        int i = startTripNumber;
        for (Trip trip : trips) {
            message.append(i).append(". Рейс №").append(trip.getTripNumber()).append(" (")
                    .append(trip.getTransport().getTransportType().getName()).append(") — ")
                    .append(trip.getTransport().getTitle()).append("\n")
                    .append("Отправление: ").append(trip.getCityFrom().getTitle()).append(" — ")
                    .append(trip.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy")
                            .withLocale(new Locale("ru")))).append("\n")
                    .append("Прибытие: ").append(trip.getCityTo().getTitle()).append(" — ")
                    .append(trip.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy")
                            .withLocale(new Locale("ru")))).append("\n\n");
            i++;
        }

        message.append("Выберите рейс для продолжения.");
        return message.toString();
    }

    public InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, Long totalTrips, int tripsPerPage) {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        for (int i = 1; i <= tripsPerPage && ((long) (currentPage - 1) * tripsPerPage + i <= totalTrips); i++) {
            int num = ((currentPage - 1) * tripsPerPage + i);
            rowInline.add(InlineKeyboardButton.builder()
                    .text(String.valueOf(num))
                    .callbackData(String.format(SELECT_TRIP_CALLBACK + "_%d", num))
                    .build());
        }

        if (!rowInline.isEmpty()) {
            rowsInline.add(rowInline);
        }

        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (currentPage > 1) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Назад")
                    .callbackData(PREV_PAGE_CALLBACK)
                    .build());
        }
        if ((long) currentPage * tripsPerPage < totalTrips) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Далее")
                    .callbackData(NEXT_PAGE_CALLBACK)
                    .build());
        }
        rowsInline.add(navigationRow);

        return InlineKeyboardMarkup.builder().keyboard(rowsInline).build();
    }

    private void handleCallbackQueryUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();

        if (NEXT_PAGE_CALLBACK.equals(data)) {
            doNextPage(absSender, chatId, messageId);
        } else if (PREV_PAGE_CALLBACK.equals(data)) {
            doPrevPage(absSender, chatId, messageId);
        } else if (data.startsWith(SELECT_TRIP_CALLBACK)) {
            int selectedTripIndex = Integer.parseInt(data.replace(SELECT_TRIP_CALLBACK + "_", ""));
            doSelectTrip(absSender, chatId, selectedTripIndex);
        } else if (CONFIRM_TRIP_CALLBACK.equals(data)) {
            doConfirmTrip(absSender, chatId, messageId);
        } else if (CANCEL_TRIP_CALLBACK.equals(data)) {
            doCancelTrip(absSender, chatId, messageId);
        }
    }

    private void doNextPage(AbsSender absSender, Long chatId, Integer messageId) throws TelegramApiException {
        clientTripService.toNextPage(chatId);
        suggestNextTripsMessage(absSender, chatId, messageId);
    }

    private void doPrevPage(AbsSender absSender, Long chatId, Integer messageId) throws TelegramApiException {
        clientTripService.toPrevPage(chatId);
        suggestNextTripsMessage(absSender, chatId, messageId);
    }

    private void doSelectTrip(AbsSender absSender, Long chatId, int selectedTripIndex) throws TelegramApiException {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);
        List<Trip> availableTrips = clientTrip.getAvailableTrips();

        int localIndex = (int) (selectedTripIndex - clientTrip.getOffset());
        if (localIndex >= 0 && localIndex <= availableTrips.size()) {
            Trip selectedTrip = availableTrips.get(localIndex - 1);

            selectedTrip.setClient(clientTrip.getClient());
            clientTripService.setTrip(chatId, selectedTrip);

            SendMessage confirmationMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Вы выбрали рейс №" + selectedTrip.getTripNumber() + "\n"
                            + "Отправление: " + selectedTrip.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy")
                            .withLocale(new Locale("ru"))) + "\n"
                            + "Место назначения: " + selectedTrip.getCityTo().getTitle())
                    .replyMarkup(buildConfirmationKeyboard())
                    .build();
            absSender.execute(confirmationMessage);
        } else {
            SendMessage errorMessage = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text("Некорректный выбор рейса.")
                    .build();
            absSender.execute(errorMessage);
        }
    }

    private ReplyKeyboard buildConfirmationKeyboard() {
        InlineKeyboardButton confirmButton = InlineKeyboardButton.builder()
                .text(Button.CONFIRM.getAlias())
                .callbackData(CONFIRM_TRIP_CALLBACK)
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(Button.CANCEL.getAlias())
                .callbackData(CANCEL_TRIP_CALLBACK)
                .build();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(Collections.singletonList(confirmButton));
        keyboard.add(Collections.singletonList(cancelButton));

        return InlineKeyboardMarkup.builder().keyboard(keyboard).build();
    }

    private void doConfirmTrip(AbsSender absSender, Long chatId, Integer messageId) throws TelegramApiException {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);
        AppContextProvider.getAppContext().getBean(TripService.class).saveTrip(clientTrip.getTrip());
        SendMessage confirmationMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Рейс сохранен в ваши рейсы")
                .build();
        absSender.execute(confirmationMessage);

        finish(absSender, chatId);
    }

    private void doCancelTrip(AbsSender absSender, Long chatId, Integer messageId) throws TelegramApiException {
        SendMessage cancelMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Выбор отменён")
                .build();
        absSender.execute(cancelMessage);

        suggestNextTripsMessage(absSender, chatId, messageId);
    }
}
