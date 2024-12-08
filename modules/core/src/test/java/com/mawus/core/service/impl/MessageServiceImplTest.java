package com.mawus.core.service.impl;

import com.mawus.core.entity.Message;
import com.mawus.core.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    public void testGetMessageByKey_Success() {
        String key = "testKey";
        Message message = new Message();
        message.setText("Test message");
        when(messageRepository.findByKey(key)).thenReturn(Optional.of(message));

        String result = messageService.getMessage(key);

        assertEquals(message.getText(), result);
        verify(messageRepository, times(1)).findByKey(key);
    }

    @Test
    public void testGetMessageByKey_NotFound() {
        String key = "testKey";
        when(messageRepository.findByKey(key)).thenReturn(Optional.empty());

        String result = messageService.getMessage(key);

        assertEquals(key, result);
        verify(messageRepository, times(1)).findByKey(key);
    }

    @Test
    public void testGetMessageByKey_Empty() {
        String result = messageService.getMessage("no_message_with_this_key");

        assertNotNull(result);
        assertEquals("no_message_with_this_key", result);
    }
}