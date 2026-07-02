package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.dto.InsectPhotoResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InsectPhotoService {
    InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file);

    List<InsectPhotoResponseDto> getPhotos(Long insectId);

    InsectPhotoResponseDto getPhoto(Long insectId, Long photoId);

    void deletePhoto(Long userId, Long insectId, Long photoId);
}
