package com.mawus.core.repository.nonpersistent.impl;

import com.mawus.core.domain.Command;
import com.mawus.core.repository.nonpersistent.ClientCommandStateRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("core_InMemoryClientCommandStateRepository")
public class InMemoryClientCommandStateRepository implements ClientCommandStateRepository {

    private final Map<Long, List<Command>> userCommands = new ConcurrentHashMap<>();

    @Override
    public void pushByChatId(Long chatId, Command command) {
        userCommands.computeIfAbsent(chatId, value -> new LinkedList<>()).add(command);
    }

    @Override
    public Command popByChatId(Long chatId) {
        List<Command> commands = userCommands.computeIfAbsent(chatId, value -> new LinkedList<>());
        if (commands.isEmpty()) {
            return null;
        }
        return commands.remove(commands.size() - 1);
    }

    @Override
    public void deleteAllByChatId(Long chatId) {
        userCommands.remove(chatId);
    }
}
