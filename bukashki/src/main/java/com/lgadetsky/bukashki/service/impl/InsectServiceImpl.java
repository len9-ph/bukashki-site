package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.InsectNotFoundException;
import com.lgadetsky.bukashki.model.entity.InsectEntity;
import com.lgadetsky.bukashki.repository.InsectRepository;
import com.lgadetsky.bukashki.service.InsectService;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class InsectServiceImpl implements InsectService {

    private final InsectRepository insectRepository;

    public InsectServiceImpl(InsectRepository insectRepository) {
        this.insectRepository = insectRepository;
    }

    @Override
    public InsectEntity createInsect(Long userId, InsectEntity newInsect) {
        newInsect.setUserId(userId);

        return insectRepository.save(newInsect);
    }

    @Override
    public void updateInsect(Long userId, InsectEntity newInsect) {
        InsectEntity entity = insectRepository.findById(newInsect.getId())
                .orElseThrow(() -> new InsectNotFoundException(newInsect.getId()));

        if (!entity.getUserId().equals(userId))
            throw new AccessDeniedException("not ur insect");

        if (newInsect.getName() != null)
            entity.setName(newInsect.getName());

        if (newInsect.getDescription() != null)
            entity.setDescription(newInsect.getDescription());

        insectRepository.save(entity);
    }

    @Override
    public InsectEntity getInsect(Long insectId) {
        InsectEntity entity = insectRepository.findById(insectId)
                .orElseThrow(() -> new InsectNotFoundException(insectId));

        return entity;
    }

    @Override
    public List<InsectEntity> getInsectsForUserId(Long userId) {
        return insectRepository.findAllByUserId(userId);
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
    public List<InsectEntity> getInsects() {
        return insectRepository.findAll();
    }

}
