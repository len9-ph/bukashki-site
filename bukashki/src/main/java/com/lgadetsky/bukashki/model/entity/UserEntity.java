package com.lgadetsky.bukashki.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "credentials_id", referencedColumnName = "id", unique = true, nullable = false)
    private UserCredentialsEntity credentials;

    public UserEntity() {
    }

    public UserEntity(Long userId, String firstName, String lastName, UserCredentialsEntity credentials) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credentials = credentials;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public UserCredentialsEntity getCredentials() {
        return credentials;
    }

    public void setCredentials(UserCredentialsEntity credentials) {
        this.credentials = credentials;
    }

    @Override
    public String toString() {
        return "UserEntity [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", credentials="
                + credentials + "]";
    }

}
