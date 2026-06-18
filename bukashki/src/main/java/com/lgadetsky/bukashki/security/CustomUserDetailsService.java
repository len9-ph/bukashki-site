package com.lgadetsky.bukashki.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;
import com.lgadetsky.bukashki.service.AccountService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    public CustomUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserCredentialsEntity credentialsEntity = accountService.getCredentialsByLogin(login);

        return new CustomUserDetails(credentialsEntity);
    }

}