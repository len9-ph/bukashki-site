package com.lgadetsky.bukashki.model.dto;

public class AvatarResponseDto {
    private String avatarUrl;

    public AvatarResponseDto(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "AvatarResponseDto{" +
                "avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
