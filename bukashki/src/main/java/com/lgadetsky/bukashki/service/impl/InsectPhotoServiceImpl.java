package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.PhotoNotFoundException;
import com.lgadetsky.bukashki.exception.StorageException;
import com.lgadetsky.bukashki.model.bean.InsectBean;
import com.lgadetsky.bukashki.model.dto.InsectPhotoResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;
import com.lgadetsky.bukashki.repository.InsectPhotoRepository;
import com.lgadetsky.bukashki.service.InsectPhotoService;
import com.lgadetsky.bukashki.service.InsectService;
import com.lgadetsky.bukashki.service.StorageService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InsectPhotoServiceImpl implements InsectPhotoService {
    private final InsectPhotoRepository insectPhotoRepository;

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
    public InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file) {
        InsectBean insectBean = insectService.getInsect(insectId);
        if (!insectBean.getUserId().equals(userId)) {
            throw new AccessDeniedException("not yours insect");
        }

        String objectKey = buildObjectKey(insectId, file);

        try (InputStream in = file.getInputStream()) {
            storageService.upload(objectKey, in, file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new StorageException("failed to upload file", e);
        }

        InsectPhotoEntity insectPhotoEntity = insectPhotoRepository.save(new InsectPhotoEntity(insectId, objectKey));

        return new InsectPhotoResponseDto(insectPhotoEntity.getId(), storageService.getUrl(objectKey));
    }

    @Override
    public List<InsectPhotoResponseDto> getPhotos(Long insectId) {
        List<InsectPhotoEntity> photoEntities = insectPhotoRepository.findAllByInsectId(insectId);

        List<InsectPhotoResponseDto> responseDtos = new ArrayList<>();

        for (InsectPhotoEntity entity : photoEntities) {
            responseDtos.add(new InsectPhotoResponseDto(entity.getId(), storageService.getUrl(entity.getObjectKey())));
        }

        return responseDtos;
    }

    @Override
    public InsectPhotoResponseDto getPhoto(Long insectId, Long photoId) {
        InsectPhotoEntity photoEntity = insectPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        if (!photoEntity.getInsectId().equals(insectId)) {
            throw new PhotoNotFoundException(photoId);
        }

        return new InsectPhotoResponseDto(photoEntity.getId(), storageService.getUrl(photoEntity.getObjectKey()));
    }

    @Override
    public void deletePhoto(Long userId, Long insectId, Long photoId) {
        InsectBean insectBean = insectService.getInsect(insectId);

        if (!userId.equals(insectBean.getUserId())) {
            throw new AccessDeniedException("not yours insect");
        }

        InsectPhotoEntity photoEntity = insectPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        if (!photoEntity.getInsectId().equals(insectId)) {
            throw new PhotoNotFoundException(photoId);
        }

        String objectKey = photoEntity.getObjectKey();

        insectPhotoRepository.deleteById(photoEntity.getId());
        storageService.delete(objectKey);
    }

    private String buildObjectKey(Long insectId, MultipartFile file) {
        String ext = ALLOWED.get(file.getContentType());
        if (ext == null) {
            throw new StorageException("Unsupported image type = " + file.getContentType());
        }
        return INSECT_STORAGE_PATH.formatted(insectId, UUID.randomUUID(), ext);
    }
}
