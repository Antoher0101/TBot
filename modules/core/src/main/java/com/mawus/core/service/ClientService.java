package com.mawus.core.service;

import com.mawus.core.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    Client findByChatId(Long chatId);

    Client findById(UUID id);

    List<Client> findAll();

    Long count();

    Long countActive();

    void saveClient(Client client);

    void updateClientName(String name, UUID id);

    void update(Client client);

    void updateClientPhone(String phone, UUID id);
}
