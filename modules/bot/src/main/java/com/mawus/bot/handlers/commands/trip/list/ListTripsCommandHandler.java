package com.mawus.bot.handlers.commands.trip.list;

import com.mawus.bot.config.BotConfig;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.app.TemplateConstants;
import com.mawus.core.app.service.TemplateService;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.Trip;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.TripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component("bot_ListTripsCommandHandler")
public class ListTripsCommandHandler implements UpdateHandler {
    protected static final String PAGE_CALLBACK = "listTrip:page_";

    protected final TemplateService templateService;
    protected final TripService tripService;
    protected final ClientService clientService;
    private final BotConfig botConfig;

    public ListTripsCommandHandler(TemplateService templateService, TripService tripService, ClientService clientService, BotConfig botConfig) {
        this.templateService = templateService;
        this.tripService = tripService;
        this.clientService = clientService;
        this.botConfig = botConfig;
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TRIPS;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return isListTripCommand(update) || isCallbackQueryUpdate(update);
    }

    private boolean isListTripCommand(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals(Button.MY_TRIPS.getAlias());
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        Long chatId = extractChatId(update);
        Client client = clientService.findByChatId(chatId);

        if (client == null) {
            return;
        }

        Integer messageId = getMessageIdFromUpdate(update);
        int currentPage = extractCurrentPage(update);
        Long pageSize = botConfig.getTripsLimit();

        handleTripsPagination(absSender, chatId, client, currentPage, pageSize, messageId);
    }

    private void handleTripsPagination(AbsSender absSender, Long chatId, Client client, int currentPage, Long pageSize, Integer messageId) throws TelegramApiException {
        long totalTrips = tripService.countTripsByClient(client.getId());
        List<Trip> trips = tripService.findByClientId(client.getId(), currentPage, Math.toIntExact(pageSize));
        String tripList = formatTrips(trips);

        if (messageId != null) {
            sendEditedMessage(absSender, chatId, messageId, tripList, currentPage, pageSize, totalTrips);
        } else {
            sendNewMessage(absSender, chatId, tripList, currentPage, pageSize, totalTrips);
        }
    }

    private void sendEditedMessage(AbsSender absSender, Long chatId, Integer messageId, String tripList, int currentPage, Long pageSize, long totalTrips) throws TelegramApiException {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(tripList)
                .replyMarkup(buildPaginationKeyboard(currentPage, pageSize, totalTrips))
                .build();
        absSender.execute(editMessage);
    }

    private void sendNewMessage(AbsSender absSender, Long chatId, String tripList, int currentPage, Long pageSize, long totalTrips) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(tripList)
                .replyMarkup(buildPaginationKeyboard(currentPage, pageSize, totalTrips))
                .build();
        absSender.execute(message);
    }

    private String formatTrips(List<Trip> trips) {
        Map<String, Object> model = new HashMap<>();
        model.put("trips", trips);

        return templateService.processTemplate(TemplateConstants.TRIP_LIST_TEMPLATE, model);
    }

    private InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, Long pageSize, long totalTrips) {
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (currentPage > 1) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Назад")
                    .callbackData(PAGE_CALLBACK + (currentPage - 1))
                    .build());
        }
        if ((long) currentPage * pageSize < totalTrips) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Далее")
                    .callbackData(PAGE_CALLBACK + (currentPage + 1))
                    .build());
        }

        return InlineKeyboardMarkup.builder().keyboard(Collections.singletonList(navigationRow)).build();
    }

    private boolean isCallbackQueryUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            return callbackData.startsWith(PAGE_CALLBACK);
        }
        return false;
    }

    private Long extractChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId();
    }

    private Integer getMessageIdFromUpdate(Update update) {
        return update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getMessageId() : null;
    }

    private int extractCurrentPage(Update update) {
        if (isCallbackQueryUpdate(update)) {
            String callbackData = update.getCallbackQuery().getData();
            return Integer.parseInt(callbackData.substring(PAGE_CALLBACK.length()));
        }
        return 1;
    }
}
