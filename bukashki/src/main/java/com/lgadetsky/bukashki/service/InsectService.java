package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.model.beans.InsectBean;
import java.util.List;
import org.springframework.lang.NonNull;

public interface InsectService {
    InsectBean createInsect(Long userId, InsectBean insectBean);

    void updateInsect(Long userId, InsectBean insectBean);

    InsectBean getInsect(@NonNull Long insectId);

    List<InsectBean> getInsectsForUserId(Long userId);

    void deleteInsect(@NonNull Long userId, @NonNull Long insectId);
}
