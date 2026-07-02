package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.service.StorageService;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity getMe(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        return user;
    }

    @Override
    public void patchUser(Long userId, UserEntity newUser) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        if (newUser.getFirstName() != null)
            user.setFirstName(newUser.getFirstName());

        if (newUser.getLastName() != null)
            user.setLastName(newUser.getLastName());

        if (newUser.getEmail() != null)
            user.setEmail(newUser.getEmail());

        userRepository.save(user);
    }
}
