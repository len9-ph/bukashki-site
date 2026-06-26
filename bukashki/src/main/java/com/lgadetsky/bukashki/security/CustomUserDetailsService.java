package com.lgadetsky.bukashki.security;

import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;
import com.lgadetsky.bukashki.repository.UserCredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialsRepository credentialsRepository;

    public CustomUserDetailsService(UserCredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserCredentialsEntity credentialsEntity = credentialsRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        return new CustomUserDetails(credentialsEntity);
    }

}