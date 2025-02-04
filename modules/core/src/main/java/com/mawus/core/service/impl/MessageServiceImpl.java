package com.mawus.core.service.impl;

import com.mawus.core.entity.Message;
import com.mawus.core.repository.MessageRepository;
import com.mawus.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("core_messageService")
public class MessageServiceImpl implements MessageService {
    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    protected final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Cacheable(value = "messages", key = "#key")
    public String getMessage(String key) {
        log.debug("Attempting to retrieve message with key: {}", key);
        Message message = messageRepository.findByKey(key).orElse(null);
        if (message == null) {
            log.warn("No message found for key: {}", key);
            return key;
        }
        log.debug("Found message with key: {}", key);
        return message.getText();
    }

    @Override
    public String getMessage(UUID id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message == null) {
            return null;
        }
        return message.getText();
    }

    @Override
    public Message updateMessage(UUID id, String newMessage, String description) {
        Message message = messageRepository.findById(id).orElse(null);
        Message updated = null;
        if (message != null) {
            message.setText(newMessage);
            message.setDescription(description);
            updated = messageRepository.save(message);
        }
        return updated;
    }

    @Override
    public Message updateMessage(String key, String newMessage, String description) {
        Message message = messageRepository.findByKey(key).orElse(null);
        Message updated = null;
        if (message != null) {
            message.setText(newMessage);
            message.setDescription(description);
            updated = messageRepository.save(message);
        }
        return updated;
    }

    @Override
    public void deleteMessage(UUID id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void deleteMessage(String key) {
        messageRepository.deleteByKey(key);
    }

    @Override
    public void createMessage(String key, String message, String description) {
        Message newMessage = new Message(key, message, description);
        messageRepository.save(newMessage);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
}
