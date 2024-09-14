package com.mawus.core.repository.nonpersistent.impl;

import com.mawus.core.domain.ClientAction;
import com.mawus.core.repository.nonpersistent.ClientActionRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryClientActionRepository implements ClientActionRepository {

    private final Map<Long, ClientAction> clientsAction = new ConcurrentHashMap<>();

    @Override
    public ClientAction findByChatId(Long chatId) {
        ClientAction clientAction = clientsAction.get(chatId);
        return SerializationUtils.clone(clientAction);
    }

    @Override
    public void updateByChatId(Long chatId, ClientAction clientAction) {
        clientsAction.put(chatId, SerializationUtils.clone(clientAction));
    }

    @Override
    public void deleteByChatId(Long chatId) {
        clientsAction.remove(chatId);
    }
}
