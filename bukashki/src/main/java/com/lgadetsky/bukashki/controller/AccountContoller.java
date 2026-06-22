package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.model.dto.UserLoginDto;
import com.lgadetsky.bukashki.model.dto.UserRegisterDto;
import com.lgadetsky.bukashki.service.AccountService;
import jakarta.validation.Valid;
import java.util.Map;
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
public class AccountContoller {
    private static final Logger LOG = LoggerFactory.getLogger(AccountContoller.class);

    private final AccountService accountService;

    public AccountContoller(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto loginDto) {
        LOG.debug("login(), loginDto = {}", loginDto);

        String token = accountService.login(loginDto.getLogin(), loginDto.getPassword());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterDto registerDto) {
        LOG.debug("register(), registerDto = {}", registerDto);

        accountService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
