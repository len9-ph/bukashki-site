package com.lgadetsky.bukashki.model.mapper;

import com.lgadetsky.bukashki.model.dto.UserRegisterDto;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.UserResponseDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.service.StorageService;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected StorageService storageService;

    @Mapping(target = "avatarUrl", expression = "java(toAvatarUrl(user.getAvatarObjectKey()))")
    public abstract UserResponseDto toDto(UserEntity user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "avatarObjectKey", ignore = true)
    public abstract UserEntity fromRegisterDto(UserRegisterDto registerDto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "avatarObjectKey", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateFromDto(UserUpdateDto updateDto, @MappingTarget UserEntity entity);

    protected String toAvatarUrl(String avatarObjectKey) {
        return avatarObjectKey == null ? null : storageService.getUrl(avatarObjectKey);
    }
}
