package com.lgadetsky.bukashki.repository;

import com.lgadetsky.bukashki.model.entity.InsectEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsectRepository extends JpaRepository<InsectEntity, Long> {
    List<InsectEntity> findAllByUserId(Long userId);
}
