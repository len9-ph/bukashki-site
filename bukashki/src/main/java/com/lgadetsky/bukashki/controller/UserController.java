package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.model.dto.AvatarResponseDto;
import com.lgadetsky.bukashki.model.dto.UserResponseDto;
import com.lgadetsky.bukashki.model.dto.UserUpdateDto;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.service.AvatarService;
import com.lgadetsky.bukashki.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final AvatarService avatarService;

    public UserController(UserService userService, AvatarService avatarService) {
        this.userService = userService;
        this.avatarService = avatarService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMe(Authentication authentication) {
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

    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AvatarResponseDto> uploadAvatar(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestPart("file") MultipartFile file) {
        AvatarResponseDto avatar = avatarService.uploadAvatar(user.getId(), file);

        return ResponseEntity.status(HttpStatus.CREATED).body(avatar);
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteAvatar(@AuthenticationPrincipal CustomUserDetails user) {
        avatarService.deleteAvatar(user.getId());

        return ResponseEntity.ok().build();
    }
}
