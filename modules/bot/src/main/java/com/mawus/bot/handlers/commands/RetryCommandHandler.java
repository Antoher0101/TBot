package com.mawus.bot.handlers.commands;

import com.mawus.bot.handlers.CommandHandler;
import com.mawus.core.domain.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RetryCommandHandler implements CommandHandler {
    private final CommandHandler originalCommand;
    private final String message;

    public RetryCommandHandler(CommandHandler originalCommand, String message) {
        this.originalCommand = originalCommand;
        this.message = message;
    }

    @Override
    public void executeCommand(AbsSender absSender, Update update, Long chatId) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        absSender.execute(sendMessage);
        originalCommand.executeCommand(absSender, update, chatId);
    }

    @Override
    public Command getCommand() {
        return Command.RETRY;
    }
}