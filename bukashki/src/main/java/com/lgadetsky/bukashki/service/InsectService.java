package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.entity.InsectEntity;
import java.util.List;
import org.springframework.lang.NonNull;

public interface InsectService {
    InsectEntity createInsect(Long userId, InsectEntity insectBean);

    void updateInsect(Long userId, InsectEntity insectBean);

    InsectEntity getInsect(Long insectId);

    List<InsectEntity> getInsects();

    List<InsectEntity> getInsectsForUserId(Long userId);

    void deleteInsect(Long userId, @NonNull Long insectId);
}
