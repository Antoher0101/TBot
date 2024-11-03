package com.mawus.bot.handlers.commands.registration;

import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.Command;
import com.mawus.core.entity.Client;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component("bot_EnterRegistrationNameCommandHandler")
public class EnterRegistrationNameCommandHandler implements CommandHandler, ActionHandler {

    private static final String ENTER_NAME_ACTION = "registration:enter-name";

    private static final Pattern NAME_PATTERN = Pattern.compile("^.*$");

    private final CommandHandlerRegistry commandHandlerRegistry;
    private final ClientCommandStateRepository clientCommandStateRepository;

    private final ClientService clientService;

    private final ClientActionRepository clientActionRepository;

    public EnterRegistrationNameCommandHandler(CommandHandlerRegistry commandHandlerRegistry, ClientCommandStateRepository clientCommandStateRepository, ClientService clientService, ClientActionRepository clientActionRepository) {
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.clientService = clientService;
        this.clientActionRepository = clientActionRepository;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && (ENTER_NAME_ACTION.equals(action));
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if ("Отмена".equals(text)) {
            sendCancelledMessage(absSender, chatId);
            return;
        }

        if (ENTER_NAME_ACTION.equals(action)) {
            handleEnterNameAction(absSender, chatId, text);
        }
        executeNextCommand(absSender, update, chatId);
    }

    private void handleEnterNameAction(AbsSender absSender, Long chatId, String name) throws TelegramApiException {
        if (!isValidName(name)) {
            sendInvalidNameMessage(absSender, chatId);
            return;
        }

        saveClientName(chatId, name);
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.ENTER_PHONE_NUMBER).executeCommand(absSender, update, chatId);
    }

    private void sendCancelledMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Ввод имени отменен")
                .build();
        absSender.execute(message);
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches(NAME_PATTERN.pattern());
    }

    private void sendInvalidNameMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Некорректное имя, попробуйте еще раз.")
                .build();
        absSender.execute(message);
    }

    private void saveClientName(Long chatId, String name) {
        Client client = clientService.findByChatId(chatId);
        if (client == null) {
            client = new Client();
            client.setChatId(chatId);
            clientService.saveClient(client);
        } else {
            clientService.updateClientName(name, client.getId());
        }
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_NAME;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_NAME_ACTION));

        sendEnterNameMessage(absSender, chatId);
        sendCurrentNameMessage(absSender, chatId);
    }

    private void sendEnterNameMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Введите имя:")
                .replyMarkup(buildReplyKeyboardMarkup())
                .build();
        absSender.execute(message);
    }

    private void sendCurrentNameMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        Client client = clientService.findByChatId(chatId);
        if (StringUtils.isBlank(client.getName())) {
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Ваше текущее имя: " + client.getName())
                .replyMarkup(buildReplyKeyboardMarkup())
                .build();
        absSender.execute(message);
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true).selective(true);

        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(Button.CANCEL.getAlias()).build()
        )));
        return keyboardBuilder.build();
    }

}
