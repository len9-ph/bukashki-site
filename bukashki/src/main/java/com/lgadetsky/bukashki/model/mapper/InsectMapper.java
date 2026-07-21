package com.lgadetsky.bukashki.model.mapper;

import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InsectMapper {
    InsectResponseDto toDto(InsectEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    InsectEntity fromCreateDto(InsectCreateDto createDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(InsectUpdateDto updateDto, @MappingTarget InsectEntity entity);
}
