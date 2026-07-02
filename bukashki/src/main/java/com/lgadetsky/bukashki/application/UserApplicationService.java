package com.lgadetsky.bukashki.application;

import com.lgadetsky.bukashki.mapper.UserMapper;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.UserResponseDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.service.StorageService;
import com.lgadetsky.bukashki.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {
    private final UserService userService;

    private final StorageService storageService;

    public UserApplicationService(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    public UserResponseDto getMe(Long userId) {
        UserEntity user = userService.getMe(userId);

        String avatarUrl = storageService.getUrl(user.getAvatarObjectKey());

        return UserMapper.toDto(user, avatarUrl);
    }

    public void patchUser(Long userId, UserUpdateDto newUser) {
        userService.patchUser(userId, UserMapper.toEntity(newUser));
    }
}
