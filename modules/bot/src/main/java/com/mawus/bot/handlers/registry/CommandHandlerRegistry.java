package com.mawus.bot.handlers.registry;

import com.mawus.core.domain.Command;
import com.mawus.bot.handlers.CommandHandler;

import java.util.List;

public interface CommandHandlerRegistry {

    void setCommandHandlers(List<CommandHandler> commandHandlers);

    CommandHandler find(Command command);
}
