package com.lgadetsky.bukashki.model.entity;

public class InsectPhotoEntity {
    private Long id;
    private Long insectId;
    private String objectKey;

    public InsectPhotoEntity() {
    }

    public InsectPhotoEntity(Long insectId, String objectKey) {
        this.insectId = insectId;
        this.objectKey = objectKey;
    }

    public InsectPhotoEntity(Long id, Long insectId, String objectKey) {
        this.id = id;
        this.insectId = insectId;
        this.objectKey = objectKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInsectId() {
        return insectId;
    }

    public void setInsectId(Long insectId) {
        this.insectId = insectId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "InsectPhotoEntity{" +
                "id=" + id +
                ", insectId=" + insectId +
                ", objectKey='" + objectKey + '\'' +
                '}';
    }
}
