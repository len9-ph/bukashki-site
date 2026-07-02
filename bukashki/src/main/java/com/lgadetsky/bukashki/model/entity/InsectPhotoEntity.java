package com.lgadetsky.bukashki.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "insect_photos")
public class InsectPhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "insect_id", nullable = false)
    private Long insectId;
    @Column(name = "object_key", nullable = false)
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
