package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
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
    public UserEntity getMe(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
    }

    @Override
    public void patchUser(Long userId, UserUpdateDto updateDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        if (updateDto.getFirstName() != null)
            user.setFirstName(updateDto.getFirstName());

        if (updateDto.getLastName() != null)
            user.setLastName(updateDto.getLastName());

        if (updateDto.getEmail() != null)
            user.setEmail(updateDto.getEmail());

        if (updateDto.getAvatarUrl() != null)
            user.setAvatarUrl(updateDto.getAvatarUrl());

        userRepository.save(user);

    }
}
