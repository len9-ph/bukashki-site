package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.InsectNotFoundException;
import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectEntity;
import com.lgadetsky.bukashki.model.mapper.InsectMapper;
import com.lgadetsky.bukashki.repository.InsectRepository;
import com.lgadetsky.bukashki.service.InsectService;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class InsectServiceImpl implements InsectService {

    private final InsectRepository insectRepository;

    private final InsectMapper insectMapper;

    public InsectServiceImpl(InsectRepository insectRepository, InsectMapper insectMapper) {
        this.insectRepository = insectRepository;
        this.insectMapper = insectMapper;
    }

    @Override
    public InsectResponseDto createInsect(Long userId, InsectCreateDto insectCreateDto) {
        InsectEntity insectEntity = insectMapper.fromCreateDto(insectCreateDto);
        insectEntity.setUserId(userId);

        return insectMapper.toDto(insectRepository.save(insectEntity));
    }

    @Override
    public void updateInsect(Long userId, InsectUpdateDto insectUpdateDto) {
        InsectEntity entity = insectRepository.findById(insectUpdateDto.getInsectId())
                .orElseThrow(() -> new InsectNotFoundException(insectUpdateDto.getInsectId()));

        if (!entity.getUserId().equals(userId))
            throw new AccessDeniedException("not ur insect");

        insectMapper.updateFromDto(insectUpdateDto, entity);

        insectRepository.save(entity);
    }

    @Override
    public InsectResponseDto getInsect(Long insectId) {
        InsectEntity entity = insectRepository.findById(insectId)
                .orElseThrow(() -> new InsectNotFoundException(insectId));

        return insectMapper.toDto(entity);
    }

    @Override
    public List<InsectResponseDto> getInsectsForUserId(Long userId) {
        return insectRepository.findAllByUserId(userId).stream()
                .map(insectMapper::toDto)
                .toList();
    }

    @Override
    public void deleteInsect(Long userId, Long insectId) {
        InsectEntity entity = insectRepository.findById(insectId)
                .orElseThrow(() -> new InsectNotFoundException(insectId));

        if (!entity.getUserId().equals(userId))
            throw new AccessDeniedException("not ur insect");

        insectRepository.deleteById(insectId);
    }

    @Override
    public List<InsectResponseDto> getInsects() {
        return insectRepository.findAll().stream()
                .map(insectMapper::toDto)
                .toList();
    }

}
