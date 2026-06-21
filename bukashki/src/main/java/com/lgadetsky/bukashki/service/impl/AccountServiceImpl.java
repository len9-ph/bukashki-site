package com.lgadetsky.bukashki.service.impl;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.exception.ResourceAlreadyExistsException;
import com.lgadetsky.bukashki.model.entity.UserCredentialsEntity;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.repository.UserCredentialsRepository;
import com.lgadetsky.bukashki.repository.UserRepository;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.security.jwt.JwtUtils;
import com.lgadetsky.bukashki.service.AccountService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;

    private final UserCredentialsRepository credentialsRepository;

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AuthenticationManager authenticationManager,
            UserCredentialsRepository credentialsRepository, UserRepository userRepository,
            JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.credentialsRepository = credentialsRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        String token = jwtUtils.generateJwtToken(userDetails);

        return token;
    }

    @Override
    public void register(UserRegisterDto userRegisterDto) {
        if (credentialsRepository.findByLogin(userRegisterDto.getLogin()).isPresent()) {
            throw new ResourceAlreadyExistsException("login already in use");
        }

        UserEntity newUser = userRepository.save(new UserEntity(userRegisterDto.getFirstName(),
                userRegisterDto.getLastName(),
                userRegisterDto.getEmail()));

        String passwordHash = passwordEncoder.encode(userRegisterDto.getPassword());

        credentialsRepository.save(new UserCredentialsEntity(newUser.getUserId(),
                userRegisterDto.getLogin(),
                passwordHash));

    }

}
