package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;

public interface UserService {
    UserEntity getUser(Long userId);

    void updateUser(UserRegisterDto dto);
}
