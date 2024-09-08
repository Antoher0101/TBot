package com.mawus.core.service;

import com.mawus.core.entity.Client;
import com.mawus.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client findByChatId(Long chatId) {
        return clientRepository.findByChatId(chatId).orElse(null);
    }

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    public void update(Client client) {
        clientRepository.updateActiveById(client.isActive(), client.getId());
    }
}
