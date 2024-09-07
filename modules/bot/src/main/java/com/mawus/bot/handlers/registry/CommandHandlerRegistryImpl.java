package com.mawus.bot.handlers.registry;

import com.mawus.bot.exceptions.HandlerNotFoundException;
import com.mawus.bot.handlers.Command;
import com.mawus.bot.handlers.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
