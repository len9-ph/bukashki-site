package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.dto.InsectPhotoResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface InsectPhotoService {
    InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file) throws AccessDeniedException;

    List<InsectPhotoResponseDto> getPhotos(Long insectId);

    InsectPhotoResponseDto getPhoto(Long id);

    void deletePhoto(Long userId, Long photoId);
}
