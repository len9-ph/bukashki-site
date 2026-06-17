package com.lgadetsky.bukashki.repository;

import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialsRepository extends JpaRepository<UserCredentialsEntity, Long> {
    Optional<UserCredentialsEntity> findByLogin(String login);
}
