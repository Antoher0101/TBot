package com.mawus.core.service;

import com.mawus.core.entity.Client;

import java.util.UUID;

public interface ClientService {
    Client findByChatId(Long chatId);

    void saveClient(Client client);

    void updateClientName(String name, UUID id);

    void update(Client client);

    void updateClientPhone(String phone, UUID id);
}
