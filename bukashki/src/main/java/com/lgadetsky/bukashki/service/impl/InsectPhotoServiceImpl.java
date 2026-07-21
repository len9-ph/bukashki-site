package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.EmptyFileException;
import com.lgadetsky.bukashki.exception.PhotoNotFoundException;
import com.lgadetsky.bukashki.exception.StorageException;
import com.lgadetsky.bukashki.exception.UnsupportedFileTypeException;
import com.lgadetsky.bukashki.model.dto.response.InsectPhotoResponseDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;
import com.lgadetsky.bukashki.model.mapper.InsectPhotoMapper;
import com.lgadetsky.bukashki.repository.InsectPhotoRepository;
import com.lgadetsky.bukashki.service.InsectPhotoService;
import com.lgadetsky.bukashki.service.InsectService;
import com.lgadetsky.bukashki.service.StorageService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class InsectPhotoServiceImpl implements InsectPhotoService {
    private final InsectPhotoRepository insectPhotoRepository;

    private final StorageService storageService;

    private final InsectService insectService;

    private final InsectPhotoMapper insectPhotoMapper;


    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private static final String INSECT_STORAGE_PATH = "insects/%d/%s.%s";

    public InsectPhotoServiceImpl(InsectPhotoRepository insectPhotoRepository, StorageService storageService,
            InsectService insectService, InsectPhotoMapper insectPhotoMapper) {
        this.insectPhotoRepository = insectPhotoRepository;
        this.storageService = storageService;
        this.insectService = insectService;
        this.insectPhotoMapper = insectPhotoMapper;
    }

    @Override
    public InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file) {
        InsectResponseDto insect = insectService.getInsect(insectId);
        if (!insect.getUserId().equals(userId)) {
            throw new AccessDeniedException("not yours insect");
        }

        String objectKey = buildObjectKey(insectId, file);

        try (InputStream in = file.getInputStream()) {
            storageService.upload(objectKey, in, file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new StorageException("failed to upload file", e);
        }

        InsectPhotoEntity insectPhotoEntity = insectPhotoRepository.save(new InsectPhotoEntity(insectId, objectKey));

        return insectPhotoMapper.toDto(insectPhotoEntity);
    }

    @Override
    public List<InsectPhotoResponseDto> getPhotos(Long insectId) {
        return insectPhotoRepository.findAllByInsectId(insectId).stream()
                .map(insectPhotoMapper::toDto)
                .toList();
    }

    @Override
    public InsectPhotoResponseDto getPhoto(Long insectId, Long photoId) {
        InsectPhotoEntity photoEntity = insectPhotoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoNotFoundException(photoId));

        if (!photoEntity.getInsectId().equals(insectId)) {
            throw new PhotoNotFoundException(photoId);
        }

        return insectPhotoMapper.toDto(photoEntity);
    }

    @Override
    public void deletePhoto(Long userId, Long insectId, Long photoId) {
        InsectResponseDto insect = insectService.getInsect(insectId);

        if (!userId.equals(insect.getUserId())) {
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
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        String ext = ALLOWED.get(file.getContentType());
        if (ext == null) {
            throw new UnsupportedFileTypeException(file.getContentType());
        }
        return INSECT_STORAGE_PATH.formatted(insectId, UUID.randomUUID(), ext);
    }
}
