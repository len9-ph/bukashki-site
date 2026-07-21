package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.UserResponseDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.model.mapper.UserMapper;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto getMe(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        return userMapper.toDto(user);
    }

    @Override
    public void patchUser(Long userId, UserUpdateDto updateDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        userMapper.updateFromDto(updateDto, user);

        userRepository.save(user);
    }
}
