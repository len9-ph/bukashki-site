package com.lgadetsky.bukashki.model;

public class UserEntity {
    private Long id;
    private String email;
    private String passwordHash;

    public UserEntity() {
        // for MyBatis
    }

    public UserEntity(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "UserEntity [id=" + id + ", email=" + email + ", passwordHash="
                + passwordHash + "]";
    }

}
