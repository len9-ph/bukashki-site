package com.lgadetsky.bukashki.model.bean;

import com.lgadetsky.bukashki.model.entity.InsectPhotoEntity;

import java.util.Objects;

public class InsectPhotoBean {
    private Long id;
    private Long insectId;
    private String objectKey;

    public InsectPhotoBean(Long id, Long insectId, String objectKey) {
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

    public static InsectPhotoBean fromEntity(InsectPhotoEntity entity) {
        Objects.requireNonNull(entity);

        return new InsectPhotoBean(entity.getId(),
                entity.getInsectId(),
                entity.getObjectKey());
    }

    public static InsectPhotoEntity toEntity(InsectPhotoBean bean) {
        Objects.requireNonNull(bean);

        return new InsectPhotoEntity(bean.getId(),
                bean.getInsectId(),
                bean.getObjectKey());
    }

    @Override
    public String toString() {
        return "InsectPhotoBean{" +
                "id=" + id +
                ", insectId=" + insectId +
                ", objectKey='" + objectKey + '\'' +
                '}';
    }
}
