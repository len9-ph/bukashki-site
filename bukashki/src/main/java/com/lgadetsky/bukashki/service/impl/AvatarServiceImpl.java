package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.EmptyFileException;
import com.lgadetsky.bukashki.exception.StorageException;
import com.lgadetsky.bukashki.exception.UnsupportedFileTypeException;
import com.lgadetsky.bukashki.exception.UserNotFoundException;
import com.lgadetsky.bukashki.model.dto.response.AvatarResponseDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.service.AvatarService;
import com.lgadetsky.bukashki.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
public class AvatarServiceImpl implements AvatarService {
    private static final Logger LOG = LoggerFactory.getLogger(AvatarServiceImpl.class);

    private final UserRepository userRepository;

    private final StorageService storageService;

    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private static final String AVATAR_STORAGE_PATH = "avatars/%d/%s.%s";

    public AvatarServiceImpl(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @Override
    public AvatarResponseDto uploadAvatar(Long userId, MultipartFile file) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        String newObjectKey = buildObjectKey(userId, file);
        String oldObjectKey = user.getAvatarObjectKey();

        try (InputStream in = file.getInputStream()) {
            storageService.upload(newObjectKey, in, file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new StorageException("failed to upload avatar", e);
        }

        user.setAvatarObjectKey(newObjectKey);
        userRepository.save(user);

        if (oldObjectKey != null) {
            try {
                storageService.delete(oldObjectKey);
            } catch (StorageException e) {
                LOG.warn("failed to delete previous avatar object {}, leaving it orphaned", oldObjectKey, e);
            }
        }

        return new AvatarResponseDto(storageService.getUrl(newObjectKey));
    }

    @Override
    public void deleteAvatar(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));

        String objectKey = user.getAvatarObjectKey();
        if (objectKey == null) {
            return;
        }

        user.setAvatarObjectKey(null);
        userRepository.save(user);

        storageService.delete(objectKey);
    }

    private String buildObjectKey(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        String ext = ALLOWED.get(file.getContentType());
        if (ext == null) {
            throw new UnsupportedFileTypeException(file.getContentType());
        }
        return AVATAR_STORAGE_PATH.formatted(userId, UUID.randomUUID(), ext);
    }
}
