package com.mawus.core.service;

import com.mawus.core.entity.Message;

public interface MessageService {

    Message findByName(String messageName);
}
