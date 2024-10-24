package com.mawus.bot.handlers.commands.trip.companion;

import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.app.service.TemplateService;
import com.mawus.core.domain.Command;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.TripService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("bot_FindCompanionsCommandHandler")
public class FindCompanionsCommandHandler implements UpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(FindCompanionsCommandHandler.class);
    private final TripService tripService;
    private final ClientService clientService;
    private final TemplateService templateService;

    public FindCompanionsCommandHandler(TripService tripService, ClientService clientService, TemplateService templateService) {
        this.tripService = tripService;
        this.clientService = clientService;
        this.templateService = templateService;
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
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("В разработке.")
                .build();
        absSender.execute(message);
    }

    @Override
    public Command getCommand() {
        return Command.COMPANIONS;
    }
}
