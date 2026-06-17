package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.entity.UserEntity;

public interface UserService {
    void register(String email, String pass);

    UserEntity findUserByEmail(String email);
}
