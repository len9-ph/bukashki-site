package com.lgadetsky.bukashki.security;

import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final UserCredentialsEntity credentials;

    public CustomUserDetails(UserCredentialsEntity credentials) {
        this.credentials = credentials;
    }

    public Long getId() {
        return credentials.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return credentials.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return credentials.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return credentials.isEnabled();
    }

}
