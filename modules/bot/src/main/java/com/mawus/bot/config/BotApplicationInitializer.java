package com.mawus.bot.config;

import com.mawus.bot.TelegramBot;
import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.commands.StartCommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.handlers.registry.CommandHandlerRegistryImpl;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.MessageService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("bot_botApplicationInitializer")
public class BotApplicationInitializer implements InitializingBean {

    private final TelegramBot telegramBot;
    private final ClientService clientService;
    private final MessageService messageService;

    protected CommandHandlerRegistry commandHandlerRegistry;
    protected List<UpdateHandler> updateHandlers;
    protected List<ActionHandler> actionHandlers;

    public BotApplicationInitializer(TelegramBot telegramBot, ClientService clientService, MessageService messageService) {
        this.telegramBot = telegramBot;
        this.clientService = clientService;
        this.messageService = messageService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initCommandHandlers();
        initUpdateHandlers();
        initActionHandlers();

        initTelegramBotHandlers();
    }

    protected void initCommandHandlers() {
        commandHandlerRegistry = new CommandHandlerRegistryImpl();
        List<CommandHandler> commandHandlers = new ArrayList<>();

        /*...*/

        commandHandlerRegistry.setCommandHandlers(commandHandlers);
    }

    protected void initUpdateHandlers() {
        updateHandlers = new ArrayList<>();

        updateHandlers.add(new StartCommandHandler(clientService, messageService));
    }

    protected void initActionHandlers() {
        actionHandlers = new ArrayList<>();
    }

    protected void initTelegramBotHandlers() {
        telegramBot.setHandlers(updateHandlers, actionHandlers);
        telegramBot.connect();
    }
}
