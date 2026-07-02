package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.dto.response.AvatarResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    AvatarResponseDto uploadAvatar(Long userId, MultipartFile file);

    void deleteAvatar(Long userId);
}
