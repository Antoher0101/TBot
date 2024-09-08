package com.mawus.core.service;

import com.mawus.core.entity.User;

public interface UserService {

    User findByChatId(Long chatId);

    User saveUser(User user);
}
