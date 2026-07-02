package com.lgadetsky.bukashki.model.dto;

import jakarta.validation.constraints.Email;

public class UserUpdateDto {
    private String firstName;

    private String lastName;
    @Email
    private String email;

    public UserUpdateDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserUpdateDto [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
    }

}
