package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.InvalidCredentialsException;
import com.lgadetsky.bukashki.exception.ResourceAlreadyExistsException;
import com.lgadetsky.bukashki.mapper.UserMapper;
import com.lgadetsky.bukashki.model.UserEntity;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void register(String email, String pass) {
        if (userMapper.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("This email already in use");
        }

        // TODO hash pass
        String passHash = pass;
        UserEntity newUser = new UserEntity(email, passHash);
        userMapper.insertUser(newUser);
    }

    @Override
    public void login(String email, String pass) {
        UserEntity realUser = userMapper.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        String passHash = pass;
        String realPassHash = realUser.getPasswordHash();

        if (!realPassHash.equals(passHash)) {
            throw new InvalidCredentialsException();
        }
    }

}
