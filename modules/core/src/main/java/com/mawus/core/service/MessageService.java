package com.mawus.core.service;

import com.mawus.core.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    String getMessage(String key);

    String getMessage(UUID id);

    List<Message> getAllMessages();

    Message updateMessage(String key, String newMessage, String description);

    Message updateMessage(UUID id, String newMessage, String description);

    void deleteMessage(String key);

    void deleteMessage(UUID id);

    void createMessage(String key, String message, String description);
}
