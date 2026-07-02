package com.lgadetsky.bukashki.service;

import org.springframework.web.multipart.MultipartFile;
import com.lgadetsky.bukashki.model.dto.response.InsectPhotoResponseDto;
import java.util.List;

public interface InsectPhotoService {
    InsectPhotoResponseDto addPhoto(Long userId, Long insectId, MultipartFile file);

    List<InsectPhotoResponseDto> getPhotos(Long insectId);

    InsectPhotoResponseDto getPhoto(Long insectId, Long photoId);

    void deletePhoto(Long userId, Long insectId, Long photoId);
}
