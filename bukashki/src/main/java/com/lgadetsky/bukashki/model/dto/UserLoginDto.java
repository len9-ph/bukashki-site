package com.lgadetsky.bukashki.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginDto {
    @NotBlank
    private String login;
    @NotBlank
    @Size(min = 8)
    private String password;

    public UserLoginDto() {
        // empty
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDto [login=" + login + ", password=" + "********" + "]";
    }

}
