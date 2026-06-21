package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.dto.UserUpdateDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getMe(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LOG.debug("getMe, userId = {}", userDetails.getId());

        return ResponseEntity.ok(userService.getMe(userDetails.getId()));

    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody @Valid UserUpdateDto updateDto, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LOG.debug("patch me, userId = {}, updateDto = {}", userDetails.getId(), updateDto);

        userService.patchUser(userDetails.getId(), updateDto);

        return ResponseEntity.ok().build();
    }
}
