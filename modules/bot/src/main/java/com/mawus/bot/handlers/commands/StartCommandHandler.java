package com.mawus.bot.handlers.commands;

import com.mawus.core.domain.Command;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.Message;
import com.mawus.core.entity.User;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.MessageService;
import com.mawus.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("bot_StartCommandHandler")
public class StartCommandHandler implements UpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(StartCommandHandler.class);
    private static final String RESTART_MESSAGE_NAME = "RESTART_MESSAGE";
    private static final String NEW_START_MESSAGE_NAME = "START_MESSAGE";

    private final ClientService clientService;
    private final UserService userService;
    private final MessageService messageService;

    public StartCommandHandler(ClientService clientService, UserService userService, MessageService messageService) {
        this.clientService = clientService;
        this.userService = userService;
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
        Client client = clientService.findByChatId(chatId);

        if (client == null) {
            createClient(chatId);
        } else {
            activateClient(client);
        }
    }

    private void sentStartMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        log.debug("The client %{} (Chat ID) started a chat with the Bot", chatId);
        String text;
        Message message;
        ReplyKeyboardMarkup keyboardMarkup = null;
        if (!isUserExists(chatId)) {
            message = messageService.findByName(NEW_START_MESSAGE_NAME);
            if (message == null) {
                log.info("Message '{}' not implemented", NEW_START_MESSAGE_NAME);
                return;
            }
            keyboardMarkup = Button.createRegisterKeyboard();
        } else {
            message = messageService.findByName(RESTART_MESSAGE_NAME);
            if (message == null) {
                log.info("Message '{}' not implemented", RESTART_MESSAGE_NAME);
                return;
            }
            keyboardMarkup = Button.createGeneralMenuKeyboard();
        }

        text = message.buildText();
        SendMessage response = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboardMarkup)
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

    private boolean isUserExists(Long chatId) {
        User user = userService.findByChatId(chatId);
        return user != null;
    }
}
