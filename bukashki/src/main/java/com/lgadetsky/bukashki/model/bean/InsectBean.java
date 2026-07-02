package com.lgadetsky.bukashki.model.bean;

import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.model.entity.InsectEntity;
import java.util.Date;
import java.util.Objects;

public class InsectBean {
    private Long id;
    private Long userId;
    private Date createdAt;
    private String name;
    private String description;

    public InsectBean(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public InsectBean(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public InsectBean(Long id, Long userId, Date createdAt, String name, String description) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static InsectBean fromEntity(InsectEntity insectEntity) {
        Objects.requireNonNull(insectEntity);

        return new InsectBean(insectEntity.getId(),
                insectEntity.getUserId(),
                insectEntity.getCreatedAt(),
                insectEntity.getName(),
                insectEntity.getDescription());
    }

    public static InsectBean fromDto(InsectResponseDto dto) {
        Objects.requireNonNull(dto);

        return new InsectBean(dto.getId(),
                dto.getUserId(),
                dto.getCreatedAt(),
                dto.getName(),
                dto.getDescription());
    }

    public static InsectResponseDto toDto(InsectBean bean) {
        Objects.requireNonNull(bean);

        return new InsectResponseDto(bean.getId(),
                bean.getUserId(),
                bean.getCreatedAt(),
                bean.getName(),
                bean.getDescription());
    }

    public static InsectEntity toEntity(InsectBean bean) {
        Objects.requireNonNull(bean);

        return new InsectEntity(bean.getUserId(),
                bean.getCreatedAt(),
                bean.getName(),
                bean.getDescription());
    }

    @Override
    public String toString() {
        return "InsectBean [id=" + id + ", userId=" + userId + ", createdAt=" + createdAt + ", name=" + name
                + ", description=" + description + "]";
    }

}
