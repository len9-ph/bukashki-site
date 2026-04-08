package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.mapper.UserMapper;
import com.lgadetsky.bukashki.model.UserEntity;
import com.lgadetsky.bukashki.service.UserService;
import java.util.Optional;

public class UserSeviceImpl implements UserService {

    private final UserMapper userMapper;

    public UserSeviceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void register(String email, String pass) {
        Optional<UserEntity> optional = userMapper.findByEmail(email);

        if (optional.isEmpty()) {
            // pass hash
            String passHash = pass;
            UserEntity newUser = new UserEntity(email, passHash);

            userMapper.insertUser(newUser);
        } else
            throw new RuntimeException();
    }

}
