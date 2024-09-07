package com.mawus.core.service.impl;

import com.mawus.core.entity.Message;
import com.mawus.core.repository.MessageRepository;
import com.mawus.core.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    protected final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message findByName(String messageName) {
        return messageRepository.findByName(messageName).orElse(null);
    }
}
