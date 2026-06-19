package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.dto.UserRegisterDto;
import com.lgadetsky.bukashki.model.entity.UserEntity;
import com.lgadetsky.bukashki.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public ResponseEntity<UserEntity> getUser(@RequestParam("userId") Long userId) {
        LOG.debug("get user, userId = {}", userId);

        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UserRegisterDto dto) {
        LOG.debug("update, registerDto = {}", dto.toString());

        userService.updateUser(dto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
