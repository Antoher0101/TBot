package com.mawus.core.service.impl;

import com.mawus.core.entity.Message;
import com.mawus.core.repository.MessageRepository;
import com.mawus.core.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("core_messageService")
public class MessageServiceImpl implements MessageService {
    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    protected final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
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
}
