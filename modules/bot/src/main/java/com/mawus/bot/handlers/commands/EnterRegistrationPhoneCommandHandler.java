package com.mawus.bot.handlers.commands;

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

@Component("bot_EnterRegistrationPhoneCommandHandler")
public class EnterRegistrationPhoneCommandHandler implements CommandHandler, ActionHandler {

    private static final String ENTER_PHONE_ACTION = "registration:enter-phone";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    private final CommandHandlerRegistry commandHandlerRegistry;

    private final ClientCommandStateRepository clientCommandStateRepository;

    private final ClientService clientService;

    private final ClientActionRepository clientActionRepository;

    public EnterRegistrationPhoneCommandHandler(CommandHandlerRegistry commandHandlerRegistry, ClientCommandStateRepository clientCommandStateRepository, ClientService clientService, ClientActionRepository clientActionRepository) {
        this.commandHandlerRegistry = commandHandlerRegistry;
        this.clientCommandStateRepository = clientCommandStateRepository;
        this.clientService = clientService;
        this.clientActionRepository = clientActionRepository;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && (ENTER_PHONE_ACTION.equals(action));
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (ENTER_PHONE_ACTION.equals(action)) {
            handleEnterPhoneAction(absSender, chatId, text);
        }

        finishRegistration(absSender, update, chatId);
    }

    private void handleEnterPhoneAction(AbsSender absSender, Long chatId, String text) throws TelegramApiException {
        if ("Пропустить".equals(text)) {
            return;
        }

        if (!isValidPhoneNumber(text)) {
            sendInvalidPhoneNumberMessage(absSender, chatId);
            return;
        }

        saveClientPhoneNumber(chatId, text);
    }

    private void finishRegistration(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.deleteAllByChatId(chatId);
        clientActionRepository.deleteByChatId(chatId);
        completeRegistration(absSender, chatId);
    }

    private void completeRegistration(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Регистрация завершена!.")
                .replyMarkup(Button.createGeneralMenuKeyboard())
                .build();
        absSender.execute(message);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches(PHONE_PATTERN.pattern());
    }

    private void sendInvalidPhoneNumberMessage(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Некорректный номер телефона, попробуйте еще раз.")
                .build();
        absSender.execute(message);
    }

    private void saveClientPhoneNumber(Long chatId, String phone) {
        Client client = clientService.findByChatId(chatId);
        if (client != null) {
            client.setPhoneNumber(phone);
            clientService.saveClient(client);
        } else {
            clientService.updateClientPhone(phone, client.getId());
        }
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_PHONE_NUMBER;
    }

    private void askForPhoneNumber(AbsSender absSender, Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Теперь введите ваш номер телефона или пропустите этот шаг.")
                .replyMarkup(buildPhoneReplyKeyboardMarkup())
                .build();
        absSender.execute(message);
    }

    private ReplyKeyboardMarkup buildPhoneReplyKeyboardMarkup() {
        ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder keyboardBuilder = ReplyKeyboardMarkup.builder();
        keyboardBuilder.resizeKeyboard(true).selective(true);

        keyboardBuilder.keyboardRow(new KeyboardRow(Arrays.asList(
                KeyboardButton.builder().text(Button.SKIP.getAlias()).build()
        )));
        return keyboardBuilder.build();
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_PHONE_ACTION));

        askForPhoneNumber(absSender, chatId);
    }
}
