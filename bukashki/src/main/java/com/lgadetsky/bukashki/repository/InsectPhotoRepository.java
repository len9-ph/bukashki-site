package com.lgadetsky.bukashki.repository;

import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsectPhotoRepository extends JpaRepository<InsectPhotoEntity, Long> {
    List<InsectPhotoEntity> findAllByInsectId(Long insectId);
}
