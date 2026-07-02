package com.lgadetsky.bukashki.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "insects")
public class InsectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "insect_name", nullable = false)
    private String name;
    @Column(name = "insect_description")
    private String description;

    public InsectEntity() {
    }

    public InsectEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public InsectEntity(Long id, String name, String description) {
        this.id = id;
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
        return "InssectEntity [id=" + id + ", userId=" + userId + ", createdAt=" + createdAt + ", name=" + name
                + ", description=" + description + "]";
    }

}
