package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.model.UserEntity;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl( PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(String email, String pass) {
        // if (userMapper.findByEmail(email).isPresent()) {
        //     throw new ResourceAlreadyExistsException("This email already in use");
        // }

        String passHash = passwordEncoder.encode(pass);
        UserEntity newUser = new UserEntity(email, passHash);
        // userMapper.insertUser(newUser);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return null;
        // return userMapper.findByEmail(email)
        //         .orElseThrow(() -> new UserNotFoundException(email));
    }

}
