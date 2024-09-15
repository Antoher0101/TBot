package com.mawus.core.service.impl;

import com.mawus.core.entity.Client;
import com.mawus.core.repository.ClientRepository;
import com.mawus.core.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client findByChatId(Long chatId) {
        return clientRepository.findByChatId(chatId).orElse(null);
    }

    @Override
    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    @Override
    public void updateClientName(String name, UUID id) {
        clientRepository.updateNameById(name, id);
    }

    @Override
    public void update(Client client) {
        clientRepository.updateActiveById(client.isActive(), client.getId());
    }

    @Override
    public void updateClientPhone(String phone, UUID id) {
        clientRepository.updatePhoneNumberById(phone, id);
    }
}
