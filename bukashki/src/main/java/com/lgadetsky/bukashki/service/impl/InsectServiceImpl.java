package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.exception.InsectNotFoundException;
import com.lgadetsky.bukashki.model.bean.InsectBean;
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
    public InsectBean createInsect(Long userId, InsectBean insectBean) {
        insectBean.setUserId(userId);

        return InsectBean.fromEntity(insectRepository.save(InsectBean.toEntity(insectBean)));
    }

    @Override
    public void updateInsect(Long userId, InsectBean insectBean) {
        InsectEntity entity = insectRepository.findById(insectBean.getId())
                .orElseThrow(() -> new InsectNotFoundException(insectBean.getId()));

        if (!entity.getUserId().equals(userId))
            throw new AccessDeniedException("not ur insect");

        if (insectBean.getName() != null)
            entity.setName(insectBean.getName());

        if (insectBean.getDescription() != null)
            entity.setDescription(insectBean.getDescription());

        insectRepository.save(entity);
    }

    @Override
    public InsectBean getInsect(Long insectId) {
        InsectEntity entity = insectRepository.findById(insectId)
                .orElseThrow(() -> new InsectNotFoundException(insectId));

        return InsectBean.fromEntity(entity);
    }

    @Override
    public List<InsectBean> getInsectsForUserId(Long userId) {
        return insectRepository.findAllByUserId(userId).stream()
                .map(InsectBean::fromEntity)
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
    public List<InsectBean> getInsects() {
        return insectRepository.findAll().stream()
                .map(InsectBean::fromEntity)
                .toList();
    }

}
