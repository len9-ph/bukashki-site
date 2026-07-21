package com.lgadetsky.bukashki.model.mapper;

import com.lgadetsky.bukashki.model.dto.response.InsectPhotoResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;
import com.lgadetsky.bukashki.service.StorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class InsectPhotoMapper {

    @Autowired
    protected StorageService storageService;

    @Mapping(target = "photoUrl", expression = "java(storageService.getUrl(entity.getObjectKey()))")
    public abstract InsectPhotoResponseDto toDto(InsectPhotoEntity entity);
}
