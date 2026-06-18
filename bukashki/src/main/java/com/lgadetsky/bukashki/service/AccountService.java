package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;

public interface AccountService {
    String login(String email, String password);

    void register(UserRegisterDto userRegisterDto);

    UserCredentialsEntity getCredentialsByLogin(String login);

}
