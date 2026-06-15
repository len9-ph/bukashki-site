package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.dto.UserLoginDto;
import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.security.jwt.JwtUtils;
import com.lgadetsky.bukashki.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto loginDto) {
        LOG.debug("login(), loginDto = {}", loginDto);

        Authentication auth = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
                );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String token = jwtUtils.generateJwtToken(userDetails);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterDto registerDto) {
        LOG.debug("register(), registerDto = {}", registerDto);

        userService.register(registerDto.getEmail(), registerDto.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
