package com.mawus.bot.config;

import com.mawus.bot.TelegramBot;
import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("bot_botApplicationInitializer")
public class BotApplicationInitializer implements InitializingBean {

    private final TelegramBot telegramBot;

    protected final List<UpdateHandler> updateHandlers;
    protected final List<ActionHandler> actionHandlers;
    protected final List<CommandHandler> commandHandlers;
    protected final CommandHandlerRegistry commandHandlerRegistry;

    public BotApplicationInitializer(TelegramBot telegramBot, List<UpdateHandler> updateHandlers, List<ActionHandler> actionHandlers, List<CommandHandler> commandHandlers, CommandHandlerRegistry commandHandlerRegistry) {
        this.telegramBot = telegramBot;

        this.updateHandlers = updateHandlers;
        this.actionHandlers = actionHandlers;
        this.commandHandlers = commandHandlers;
        this.commandHandlerRegistry = commandHandlerRegistry;

        this.commandHandlerRegistry.setCommandHandlers(this.commandHandlers);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        telegramBot.setHandlers(updateHandlers, actionHandlers);
        telegramBot.connect();
    }
}
