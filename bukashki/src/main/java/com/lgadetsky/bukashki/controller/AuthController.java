package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterDto userDto) {
        LOG.debug("register(), userDto = {}", userDto.toString());

        userService.register(userDto.getEmail(), userDto.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
