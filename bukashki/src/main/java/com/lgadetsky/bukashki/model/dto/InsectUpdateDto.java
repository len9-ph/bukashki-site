package com.lgadetsky.bukashki.model.dto;

import jakarta.validation.constraints.NotBlank;

public class InsectUpdateDto {
    @NotBlank
    private String name;
    private String description;

    public InsectUpdateDto() {
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
        return "InsectUpdateDto [name=" + name + ", description=" + description + "]";
    }

}
