package com.mawus.api.service;

import com.mawus.api.dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserById(UUID id);
}
