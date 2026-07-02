package com.lgadetsky.bukashki.mapper;

import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectEntity;

public class InsectMapper {
    public static InsectResponseDto toDto(InsectEntity entity) {
        if (entity == null)
            return null;

        return new InsectResponseDto(entity.getId(),
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getName(),
                entity.getDescription());
    }

    public static InsectEntity toEntity(InsectCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        return new InsectEntity(createDto.getName(),
                createDto.getDescription());
    }

    public static InsectEntity toEntity(InsectUpdateDto updateDto) {
        if (updateDto == null) {
            return null;
        }

        return new InsectEntity(updateDto.getInsectId(),
                updateDto.getName(),
                updateDto.getDescription());
    }
}
