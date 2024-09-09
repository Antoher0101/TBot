package com.mawus.bot.handlers.commands;

import com.mawus.bot.exceptions.ClientNotFoundException;
import com.mawus.bot.handlers.Command;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.model.Button;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.User;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.UserService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RegistrationCommandHandler implements UpdateHandler {

    private final UserService userService;
    private final ClientService clientService;

    public RegistrationCommandHandler(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @Override
    public Command getCommand() {
        return Command.REGISTRATION;
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return isRegistrationRequestUpdate(update);
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        if (isRegistrationRequestUpdate(update)) {
            User user = userService.findByChatId(update.getMessage().getChatId());

            if (user == null) {
                createNewUser(update);
                handleRegisterRequestAction(absSender, update.getMessage().getChatId());
            } else {
                // Пользователь уже зарегистрирован. Мб он хочет изменить данные?
            }
        }
    }

    private void handleRegisterRequestAction(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите данные:")
                .replyMarkup(Button.createRegisterMenuKeyboard())
                .build();
        absSender.execute(message);
    }

    private boolean isRegistrationRequestUpdate(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equals(Button.REGISTER.getAlias());
    }

    private User createNewUser(Update update) {
        Client client = clientService.findByChatId(update.getMessage().getChatId());
        if (client == null) {
            throw new ClientNotFoundException();
        }
        User newUser = new User();
        newUser.setUsername(update.getMessage().getFrom().getUserName() != null ?
                update.getMessage().getFrom().getUserName() :
                update.getMessage().getFrom().getFirstName());
        newUser.setClient(client);
        return userService.saveUser(newUser);
    }
}
