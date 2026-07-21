package com.lgadetsky.bukashki.service;

import java.util.List;

import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;

public interface InsectService {
    InsectResponseDto createInsect(Long userId, InsectCreateDto insectCreateDto);

    void updateInsect(Long userId, InsectUpdateDto insectUpdateDto);

    InsectResponseDto getInsect(Long insectId);

    List<InsectResponseDto> getInsects();

    List<InsectResponseDto> getInsectsForUserId(Long userId);

    void deleteInsect(Long userId, Long insectId);
}
