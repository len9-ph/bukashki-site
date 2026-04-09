package com.lgadetsky.bukashki.service;

public interface UserService {
    void register(String email, String pass);

    void login(String email, String pass);
}
