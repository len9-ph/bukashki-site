package com.lgadetsky.bukashki.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void register(String email, String pass);
}
