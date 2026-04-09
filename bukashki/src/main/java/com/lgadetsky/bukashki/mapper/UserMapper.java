package com.lgadetsky.bukashki.mapper;

import com.lgadetsky.bukashki.model.UserEntity;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(UserEntity user);

    Optional<UserEntity> findByEmail(String email);
}
