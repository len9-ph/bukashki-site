package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.bean.InsectBean;
import java.util.List;
import org.springframework.lang.NonNull;

public interface InsectService {
    InsectBean createInsect(Long userId, InsectBean insectBean);

    void updateInsect(Long userId, InsectBean insectBean);

    InsectBean getInsect(Long insectId);

    List<InsectBean> getInsects();

    List<InsectBean> getInsectsForUserId(Long userId);

    void deleteInsect(Long userId, @NonNull Long insectId);
}
