package com.lgadetsky.bukashki.model.dto.response;

public class InsectPhotoResponseDto {
    private Long id;
    private String photoUrl;

    public InsectPhotoResponseDto(Long id, String photoUrl) {
        this.id = id;
        this.photoUrl = photoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "InsectPhotoResponseDto{" +
                "id=" + id +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
