package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.Command;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.Message;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommandHandler implements UpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(StartCommandHandler.class);
    private static final String START_MESSAGE_NAME = "START_MESSAGE";

    private final ClientService clientService;

    private final MessageService messageService;

    public StartCommandHandler(ClientService clientService, MessageService messageService) {
        this.clientService = clientService;
        this.messageService = messageService;
    }

    @Override
    public Command getCommand() {
        return Command.START;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().startsWith(Button.START.getAlias());
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();

        saveClient(chatId);
        sentStartMessage(absSender, chatId);
    }

    private void saveClient(Long chatId) {
        Client client = clientService.findByChatId(chatId).orElse(null);

        if (client == null) {
            createClient(chatId);
        } else {
            activateClient(client);
        }
    }

    private void sentStartMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        log.debug("The client %{} (Chat ID) started a chat with the Bot", chatId);
        String text;
        Message message = messageService.findByName(START_MESSAGE_NAME);

        if (message == null) {
            log.info("Message '{}' not implemented", START_MESSAGE_NAME);
            return;
        }
        text = message.buildText();
        SendMessage response = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(Button.createGeneralMenuKeyboard())
                .build();
        absSender.execute(response);
    }

    private void createClient(Long chatId) {
        Client client = new Client();
        client.setChatId(chatId);
        client.setActive(true);
        clientService.saveClient(client);

        log.info("New client {} (Chat ID) activated", chatId);
    }

    private void activateClient(Client client) {
        client.setActive(true);
        clientService.update(client);

        log.info("Client activated {} (Chat ID)", client.getChatId());
    }
}
