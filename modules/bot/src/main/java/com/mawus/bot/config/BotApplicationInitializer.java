package com.mawus.bot.config;

import com.mawus.bot.TelegramBot;
import com.mawus.bot.handlers.ActionHandler;
import com.mawus.bot.handlers.CommandHandler;
import com.mawus.bot.handlers.UpdateHandler;
import com.mawus.bot.handlers.commands.EnterRegistrationPhoneCommand;
import com.mawus.bot.handlers.commands.RegistrationCommandHandler;
import com.mawus.bot.handlers.commands.EnterRegistrationNameCommandHandler;
import com.mawus.bot.handlers.commands.StartCommandHandler;
import com.mawus.bot.handlers.registry.CommandHandlerRegistry;
import com.mawus.bot.handlers.registry.CommandHandlerRegistryImpl;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import com.mawus.core.service.ClientService;
import com.mawus.core.service.MessageService;
import com.mawus.core.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("bot_botApplicationInitializer")
public class BotApplicationInitializer implements InitializingBean {

    private final TelegramBot telegramBot;
    private final ClientService clientService;
    private final UserService userService;
    private final MessageService messageService;

    protected ClientActionRepository clientActionRepository;
    protected CommandHandlerRegistry commandHandlerRegistry;
    protected ClientCommandStateRepository clientCommandStateRepository;
    protected List<UpdateHandler> updateHandlers;
    protected List<ActionHandler> actionHandlers;

    public BotApplicationInitializer(TelegramBot telegramBot, ClientService clientService, UserService userService, MessageService messageService,
                                     ClientActionRepository clientActionRepository, ClientCommandStateRepository clientCommandStateRepository) {
        this.telegramBot = telegramBot;
        this.clientService = clientService;
        this.userService = userService;
        this.messageService = messageService;
        this.clientActionRepository = clientActionRepository;
        this.clientCommandStateRepository = clientCommandStateRepository;
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

        commandHandlers.add(new EnterRegistrationNameCommandHandler(commandHandlerRegistry, clientCommandStateRepository, clientService, clientActionRepository));
        commandHandlers.add(new EnterRegistrationPhoneCommand(commandHandlerRegistry, clientCommandStateRepository, clientService, clientActionRepository));

        commandHandlerRegistry.setCommandHandlers(commandHandlers);
    }

    protected void initUpdateHandlers() {
        updateHandlers = new ArrayList<>();

        updateHandlers.add(new StartCommandHandler(clientService, userService, messageService));
        updateHandlers.add(new RegistrationCommandHandler(commandHandlerRegistry, clientCommandStateRepository, userService, clientService));
    }

    protected void initActionHandlers() {
        actionHandlers = new ArrayList<>();

        actionHandlers.add(new EnterRegistrationNameCommandHandler(commandHandlerRegistry, clientCommandStateRepository, clientService, clientActionRepository));
        actionHandlers.add(new EnterRegistrationPhoneCommand(commandHandlerRegistry, clientCommandStateRepository, clientService, clientActionRepository));
    }

    protected void initTelegramBotHandlers() {
        telegramBot.setHandlers(updateHandlers, actionHandlers);
        telegramBot.connect();
    }
}
