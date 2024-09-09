package com.mawus.core.service.impl;

import com.mawus.core.entity.User;
import com.mawus.core.repository.UserRepository;
import com.mawus.core.service.UserService;
import org.springframework.stereotype.Service;

@Service("core_userService")
public class UserServiceImpl implements UserService {

    protected final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByChatId(Long chatId) {
        return userRepository.findByClient_ChatId(chatId).orElse(null);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
