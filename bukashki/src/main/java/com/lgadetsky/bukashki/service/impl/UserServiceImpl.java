package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.dto.UserResponseDto;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.service.StorageService;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final StorageService storageService;

    public UserServiceImpl(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @Override
    public UserResponseDto getMe(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        String avatarUrl = user.getAvatarObjectKey() == null
                ? null
                : storageService.getUrl(user.getAvatarObjectKey());

        return new UserResponseDto(user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                avatarUrl);
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

        userRepository.save(user);

    }
}
