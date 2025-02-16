package com.mawus.bot.handlers.commands.registration;

import com.mawus.bot.exceptions.ClientNotFoundException;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Client;
import com.mawus.core.entity.User;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.MessageService;
import com.mawus.core.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component("bot_RegistrationCommandHandler")
public class RegistrationCommandHandler implements UpdateHandler {

    private final CommandHandlerRegistry commandHandlerRegistry;
    private final ClientCommandStateRepository clientCommandStateRepository;
    private final UserService userService;
    private final ClientService clientService;
    private final MessageService messageService;

    public RegistrationCommandHandler(CommandHandlerRegistry commandHandlerRegistry,
                                      ClientCommandStateRepository clientCommandStateRepository,
                                      UserService userService,
                                      ClientService clientService,
                                      MessageService messageService) {
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.userService = userService;
        this.clientService = clientService;
        this.messageService = messageService;
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
        User user = userService.findByChatId(update.getMessage().getChatId());

        if (user == null) {
            createNewUser(update);
            executeNextCommand(absSender, update, update.getMessage().getChatId());
        } else {
            // Пользователь уже зарегистрирован. Мб он хочет изменить данные? todo после MVP
            sendAlreadyRegisteredMessage(absSender, update.getMessage().getChatId());
        }
    }

    private User createNewUser(Update update) {
        Client client = clientService.findByChatId(update.getMessage().getChatId());
        if (client == null) {
            throw new ClientNotFoundException();
        }
        User newUser = new User();
        if (update.getMessage().getFrom().getUserName() != null) {
            newUser.setUsername(update.getMessage().getFrom().getUserName());
            newUser.setHasLink(true);
        }
        else {
            newUser.setUsername(update.getMessage().getFrom().getId().toString());
            newUser.setHasLink(false);
        }
        newUser.setClient(client);
        return userService.saveUser(newUser);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.ENTER_NAME).executeCommand(absSender, update, chatId);
    }

    private void sendAlreadyRegisteredMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageService.getMessage("bot.registration.alreadyRegistered.message"))
                .build();
        absSender.execute(message);
    }

    private boolean isRegistrationRequestUpdate(Update update) {
        return update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText().equals(Button.REGISTER.getAlias());
    }
}
