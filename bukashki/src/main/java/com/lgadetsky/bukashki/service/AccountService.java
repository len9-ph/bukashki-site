package com.lgadetsky.bukashki.service;

import com.lgadetsky.bukashki.dto.UserRegisterDto;

public interface AccountService {
    String login(String email, String password);

    void register(UserRegisterDto userRegisterDto);

}
