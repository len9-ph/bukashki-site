package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;

public interface UserService {
    UserEntity getMe(Long userId);

    void patchUser(Long userId, UserUpdateDto updateDto);
}
