package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.dto.UserResponseDto;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;

public interface UserService {
    UserResponseDto getMe(Long userId);

    void patchUser(Long userId, UserUpdateDto updateDto);
}
