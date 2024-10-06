package com.mawus.bot.handlers.commands.trip;

import com.mawus.bot.exceptions.ClientNotFoundException;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientTrip;
import com.mawus.core.domain.Command;
import com.mawus.core.domain.TripQuery;
import com.mawus.core.entity.Client;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.ClientTripService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("bot_AddTripCommandHandler")
public class AddTripCommandHandler implements UpdateHandler {

    private final ClientTripService clientTripService;
    private final ClientCommandStateRepository clientCommandStateRepository;

    private final CommandHandlerRegistry commandHandlerRegistry;
    private final ClientService clientService;

    public AddTripCommandHandler(ClientTripService clientTripService, ClientCommandStateRepository clientCommandStateRepository, CommandHandlerRegistry commandHandlerRegistry, ClientService clientService) {
        this.clientTripService = clientTripService;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientService = clientService;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equals(Button.ADD_TRIP.getAlias());
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();

        ClientTrip clientTrip = clientTripService.findTripByChatId(chatId);
        if (clientTrip != null) {
            return;
        }

        createNewDraftTrip(chatId);

        executeNextCommand(absSender, update, chatId);
    }

    private void createNewDraftTrip(Long chatId) {
        ClientTrip clientTrip;
        TripQuery trip = new TripQuery();
        Client client = clientService.findByChatId(chatId);
        if (client == null) {
            throw new ClientNotFoundException();
        }
        clientTrip = new ClientTrip(trip, client);
        clientTripService.createDraftTrip(chatId, clientTrip);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.SELECT_TRANSPORT).executeCommand(absSender, update, chatId);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TRIP;
    }
}
