package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto getMe(Long userId);

    void patchUser(Long userId, UserUpdateDto updateDto);
}
