package com.lgadetsky.bukashki.repository;

import com.lgadetsky.bukashki.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
}
