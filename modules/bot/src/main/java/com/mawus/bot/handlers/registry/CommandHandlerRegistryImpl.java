package com.mawus.bot.handlers.registry;

import com.mawus.bot.exceptions.HandlerNotFoundException;
import com.mawus.core.domain.Command;
import com.mawus.bot.handlers.CommandHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("bot_CommandHandlerRegistry")
public class CommandHandlerRegistryImpl implements CommandHandlerRegistry {

    private Map<Command, CommandHandler> commandHandlers;

    @Override
    public void setCommandHandlers(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers.stream().collect(
                Collectors.toMap(CommandHandler::getCommand, Function.identity())
        );
    }

    @Override
    public CommandHandler find(Command command) {
        CommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler == null) {
            throw new HandlerNotFoundException("CommandHandler with name '" + command + "' not found");
        }
        return commandHandler;
    }
}
