package com.mawus.bot.handlers.commands.trip.add;

import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.commands.base.AbstractTripAction;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.model.Button;
import com.mawus.core.domain.ClientAction;
import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientTripService;
import io.github.dostonhamrakulov.InlineCalendarBuilder;
import io.github.dostonhamrakulov.localization.LanguageEnum;
import io.github.dostonhamrakulov.utils.InlineCalendarCommandUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component("bot_EnterDepartureDateAction")
public class EnterDepartureDateAction extends AbstractTripAction implements UpdateHandler {

    private static final String ENTER_DEPARTURE_DATE_ACTION = "addTrip:enter-departure-date";
    private static final String DATE_ACTION = "addTrip:enter-date";

    private final InlineCalendarBuilder inlineCalendarBuilder = new InlineCalendarBuilder(LanguageEnum.RU);
    private final ClientTripService clientTripService;

    public EnterDepartureDateAction(ClientActionRepository clientActionRepository,
                                    ClientTripService clientTripService,
                                    ClientCommandStateRepository clientCommandStateRepository,
                                    CommandHandlerRegistry commandHandlerRegistry) {
        super(clientActionRepository, clientCommandStateRepository, commandHandlerRegistry, clientTripService);
        this.clientTripService = clientTripService;
    }

    @Override
    public boolean canHandleAction(Update update, String action) {
        return update.hasMessage() && update.getMessage().hasText() && ENTER_DEPARTURE_DATE_ACTION.equals(action);
    }

    @Override
    public boolean canHandleUpdate(Update update) {
        return update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith(DATE_ACTION);
    }

    @Override
    public void handleUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        handleCallbackQueryUpdate(absSender, update);
    }

    private void handleCallbackQueryUpdate(AbsSender absSender, Update update) throws TelegramApiException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String query = update.getCallbackQuery().getData().replaceAll(DATE_ACTION + ":", "");
        update.getCallbackQuery().setData(query);
        if (InlineCalendarCommandUtil.isInlineCalendarClicked(update)) {
            if (InlineCalendarCommandUtil.isCalendarNavigationButtonClicked(update)) {
                InlineKeyboardMarkup updatedCalendar = buildCalendar(update);
                absSender.execute(SendMessage.builder()
                        .chatId(chatId)
                        .replyMarkup(updatedCalendar)
                        .text("Выберите дату отправления:")
                        .build());
                return;
            }

            if (InlineCalendarCommandUtil.isCalendarIgnoreButtonClicked(update)) {
                return;
            }

            LocalDate selectedDate = InlineCalendarCommandUtil.extractDate(update);
            clientTripService.updateTripDate(chatId, selectedDate);
            executeNextCommand(absSender, update, chatId);
        }
    }

    @Override
    public void handleAction(AbsSender absSender, Update update, String action) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (Button.CANCEL.getAlias().equals(text)) {
            finish(absSender, chatId);
            sendCancelledMessage(absSender, chatId);
        } else {
            sendEnterDepartureDateMessage(absSender, update, chatId);
        }
    }

    private void executeNextCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientCommandStateRepository.pushByChatId(chatId, getCommand());
        commandHandlerRegistry.find(Command.SELECT_TRIP).executeCommand(absSender, update, chatId);
    }

    @Override
    public Command getCommand() {
        return Command.ENTER_DEPARTURE_DATE;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        clientActionRepository.updateByChatId(chatId, new ClientAction(getCommand(), ENTER_DEPARTURE_DATE_ACTION));
        sendEnterDepartureDateMessage(absSender, update, chatId);
    }

    private void sendEnterDepartureDateMessage(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        InlineKeyboardMarkup calendarMarkup = buildCalendar(update);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Выберите дату отправления:")
                .replyMarkup(calendarMarkup)
                .build();
        absSender.execute(message);
    }

    private InlineKeyboardMarkup buildCalendar(Update update) {
        return inlineCalendarBuilder.customPrefix(DATE_ACTION).setMinDate(LocalDate.now()).build(update);
    }
}