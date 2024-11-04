package com.mawus.bot.handlers.commands.trip.companion;

import com.mawus.bot.config.BotConfig;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.app.TemplateConstants;
import com.mawus.core.app.service.TemplateService;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.Trip;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.ClientTripService;
import com.mawus.core.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component("bot_FindCompanionsCommandHandler")
public class FindCompanionsCommandHandler implements CommandHandler, UpdateHandler {
    protected static final String PAGE_CALLBACK = "companions:page";

    private static final Logger log = LoggerFactory.getLogger(FindCompanionsCommandHandler.class);
    private final TripService tripService;
    private final ClientService clientService;
    private final TemplateService templateService;
    private final BotConfig botConfig;
    private final ClientTripService clientTripService;

    public FindCompanionsCommandHandler(TripService tripService, ClientService clientService, TemplateService templateService, BotConfig botConfig, ClientTripService clientTripService) {
        this.tripService = tripService;
        this.clientService = clientService;
        this.templateService = templateService;
        this.botConfig = botConfig;
        this.clientTripService = clientTripService;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            Client client = clientService.findByChatId(chatId);
            Integer messageId = getMessageIdFromUpdate(update);
            int currentPage = 1;
            Long pageSize = botConfig.getTripsLimit();
            handleWithPagination(absSender, chatId, client, currentPage, pageSize, messageId);
        } else {
            //todo
        }
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return update.hasMessage()
               && update.getMessage().hasText()
               && update.getMessage().getText().equals(Button.COMPANIONS.getAlias());
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Client client = clientService.findByChatId(chatId);

        if (client == null) {
            return;
        }

        Integer messageId = getMessageIdFromUpdate(update);
        int currentPage = extractCurrentPage(update);
        Long pageSize = botConfig.getTripsLimit();

        handleWithPagination(absSender, chatId, client, currentPage, pageSize, messageId);
    }

    private void handleWithPagination(AbsSender absSender, Long chatId, Client client, int currentPage, Long pageSize, Integer messageId) throws TelegramApiException {
        Trip selectedTrip = tripService.loadIntermediateStations(clientTripService.getTrip(chatId));

        List<Trip> companionTrips = tripService.findCompanions(selectedTrip);
        String tripList = formatTrips(companionTrips, currentPage, pageSize);

        if (messageId != null) {
            sendEditedMessage(absSender, chatId, messageId, tripList, currentPage, pageSize, companionTrips.size());
        } else {
            sendNewMessage(absSender, chatId, tripList, currentPage, pageSize, companionTrips.size());
        }
    }

    private String formatTrips(List<Trip> companionTrips, int currentPage, Long pageSize) {
        Map<String, Object> model = new HashMap<>();
        model.put("trips", companionTrips);
        model.put("page", currentPage);
        model.put("pageSize", pageSize);

        return templateService.processTemplate(TemplateConstants.COMPANION_LIST_TEMPLATE, model);
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

    private InlineKeyboardMarkup buildPaginationKeyboard(int currentPage, Long pageSize, long totalTrips) {
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (currentPage > 1) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Назад")
                    .callbackData(String.format(PAGE_CALLBACK + "_%d", (currentPage - 1)))
                    .build());
        }
        if ((long) currentPage * pageSize < totalTrips) {
            navigationRow.add(InlineKeyboardButton.builder()
                    .text("Далее")
                    .callbackData(String.format(PAGE_CALLBACK + "_%d", (currentPage + 1)))
                    .build());
        }
        return InlineKeyboardMarkup.builder().keyboard(Collections.singletonList(navigationRow)).build();
    }

    @Override
    public Command getCommand() {
        return Command.COMPANIONS;
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

    private boolean isCallbackQueryUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            return callbackData.startsWith(PAGE_CALLBACK);
        }
        return false;
    }
}
