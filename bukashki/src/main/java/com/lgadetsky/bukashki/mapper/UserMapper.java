package com.lgadetsky.bukashki.mapper;

import com.lgadetsky.bukashki.model.dto.UserRegisterDto;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.UserResponseDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;

public class UserMapper {
    public static UserResponseDto toDto(UserEntity entity, String avatarUrl) {
        if (entity == null)
            return null;

        return new UserResponseDto(entity.getUserId(), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), avatarUrl);
    }

    public static UserEntity toEntity(UserRegisterDto dto) {
        if (dto == null)
            return null;

        return new UserEntity(dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail());
    }

    public static UserEntity toEntity(UserUpdateDto dto) {
        if (dto == null)
            return null;

        return new UserEntity(dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail());
    }
}
