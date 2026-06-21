package com.lgadetsky.bukashki.model.dto;

import java.util.Date;

public class InsectDto {
    private Long id;
    private Long userId;
    private Date createdAt;
    private String name;
    private String description;

    public InsectDto() {
    }

    public InsectDto(Long id, Long userId, Date createdAt, String name, String description) {
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

    @Override
    public String toString() {
        return "InsectEntityDto [id=" + id + ", userId=" + userId + ", createdAt=" + createdAt + ", name=" + name
                + ", description=" + description + "]";
    }

}
