package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.StorageException;
import com.lgadetsky.bukashki.model.bean.InsectBean;
import com.lgadetsky.bukashki.model.dto.InsectPhotoResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectEntity;
import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;
import com.lgadetsky.bukashki.repository.InsectPhotoRepository;
import com.lgadetsky.bukashki.repository.InsectRepository;
import com.lgadetsky.bukashki.service.InsectPhotoService;
import com.lgadetsky.bukashki.service.InsectService;
import com.lgadetsky.bukashki.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InsectPhotoServiceImpl implements InsectPhotoService {
    private static InsectPhotoRepository insectPhotoRepository;

    private final StorageService storageService;

    private final InsectService insectService;

    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private static final String INSECT_STORAGE_PATH = "insects/%d/%s.%s";

    public InsectPhotoServiceImpl(InsectPhotoRepository insectPhotoRepository, StorageService storageService, InsectService insectService) {
        this.insectPhotoRepository = insectPhotoRepository;
        this.storageService = storageService;
        this.insectService = insectService;
    }

    @Override
    public InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file) throws AccessDeniedException {
        InsectBean insectBean = insectService.getInsect(insectId);
        if (!insectBean.getUserId().equals(userId)) {
            throw new AccessDeniedException("not yours insect");
        }

        String objectKey = buildObjectKey(insectId, file);

        InsectPhotoEntity insectPhotoEntity = insectPhotoRepository.save(new InsectPhotoEntity(insectId, objectKey));

        try {
            storageService.upload(objectKey, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new StorageException("file input stream exception");
        }


        return null;
    }

    @Override
    public List<InsectPhotoResponseDto> getPhotos(Long insectId) {
        return List.of();
    }

    @Override
    public InsectPhotoResponseDto getPhoto(Long id) {
        return null;
    }

    @Override
    public void deletePhoto(Long userId, Long photoId) {

    }

    private String buildObjectKey(Long insectId, MultipartFile file) {
        String ext = ALLOWED.get(file.getContentType());
        if (ext == null) {
            throw new StorageException("Unsupported image type = " + file.getContentType());
        }
        return INSECT_STORAGE_PATH.formatted(insectId, UUID.randomUUID(), ext);
    }
}
