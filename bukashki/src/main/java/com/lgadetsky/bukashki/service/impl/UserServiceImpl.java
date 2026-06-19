package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    @Override
    public void updateUser(UserRegisterDto dto) {
        userRepository.save(new UserEntity(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getAvatarUrl()));
    }
}
