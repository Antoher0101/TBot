package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Trip;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.repository.nonpersistent.ClientTripStateRepository;
import com.mawus.core.service.ClientTripService;
import com.mawus.raspAPI.exceptions.HTTPClientException;
import com.mawus.raspAPI.exceptions.ParserException;
import com.mawus.raspAPI.exceptions.ValidationException;
import com.mawus.raspAPI.services.TripRequestService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component("bot_SelectTripCommandHandler")
public class SelectTripCommandHandler extends AbstractTripAction {
    protected static final String SELECT_TRIP_ACTION = "addTrip:select-trip";

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
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && SELECT_TRIP_ACTION.equals(action);
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (Button.CANCEL.getAlias().equals(text)) {
            finish(chatId);
            sendCancelledMessage(absSender, chatId);
            return;
        }

//        handleEnterDepartureAction(absSender, chatId, text);
//        executeNextCommand(absSender, update, chatId);
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), SELECT_TRIP_ACTION));
        suggestNextTripsMessage(absSender, chatId);
    }

    private void suggestNextTripsMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);
        String text;
        try {
            text = formatTrips(tripRequestService.fetchNextStations(clientTrip.getTripQuery(), 0).getTrips());
        } catch (ParserException | ValidationException | HTTPClientException e) {
            throw new RuntimeException(e);
        }
        SendMessage message = SendMessage.builder().text(text).build();
        absSender.execute(message);
    }

    @Override
    public Command getCommand() {
        return Command.SELECT_TRIP;
    }

    public String formatTrips(Collection<Trip> trips) {
        StringBuilder message = new StringBuilder();
        message.append("Список доступных рейсов:\n\n");
        int i = 1;
        for (Trip trip : trips) {
            message.append(i).append(". ")
                    .append("Рейс №").append(trip.getTripNumber()).append(" (")
                    .append(trip.getTransport().getTransportType()).append(") — ")
                    .append(trip.getTransport().getTile()).append("\n")
                    .append("Отправление: ").append(trip.getCityFrom().getTitle()).append(" — ")
                    .append(trip.getDepartureTime().format(DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy"))).append("\n")
                    .append("Прибытие: ").append(trip.getCityTo().getTitle()).append(" — ")
                    .append(trip.getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm, dd MMM yyyy"))).append("\n");

            message.append("\n");
            i++;
        }

        message.append("Выберите номер рейса или нажмите 'Далее' для загрузки следующих вариантов.");
        return message.toString();
    }
}
