package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.model.dto.response.InsectPhotoResponseDto;
import com.lgadetsky.bukashki.model.dto.response.InsectResponseDto;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.service.InsectPhotoService;
import com.lgadetsky.bukashki.service.InsectService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/insects")
public class InsectController {
    private final Logger LOG = LoggerFactory.getLogger(InsectController.class);

    private final InsectService insectService;

    private final InsectPhotoService insectPhotoService;

    public InsectController(InsectService insectService, InsectPhotoService insectPhotoService) {
        this.insectService = insectService;
        this.insectPhotoService = insectPhotoService;
    }

    @GetMapping
    public ResponseEntity<List<InsectResponseDto>> getInsects() {
        return ResponseEntity.ok(insectService.getInsects());
    }

    @GetMapping("/{insectId}")
    public ResponseEntity<InsectResponseDto> getInsect(@PathVariable Long insectId) {
        return ResponseEntity.ok(insectService.getInsect(insectId));
    }

    @PostMapping
    public ResponseEntity<Void> createInsect(@AuthenticationPrincipal CustomUserDetails user,
                                             @RequestBody() @Valid InsectCreateDto dto) {
        insectService.createInsect(user.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateInsect(@AuthenticationPrincipal CustomUserDetails user,
                                             @RequestBody @Valid InsectUpdateDto dto) {
        insectService.updateInsect(user.getId(), dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<InsectResponseDto>> getInsects(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(insectService.getInsectsForUserId(user.getId()));
    }

    @DeleteMapping("/{insectId}")
    public ResponseEntity<Void> deleteInsect(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable Long insectId) {
        insectService.deleteInsect(user.getId(), insectId);

        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/{insectId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<InsectPhotoResponseDto> uploadPhoto(@AuthenticationPrincipal CustomUserDetails user,
            @RequestPart("file") MultipartFile file,
            @PathVariable Long insectId
    ) {
        InsectPhotoResponseDto photo = insectPhotoService.addPhoto(user.getId(), insectId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(photo);
    }

    @GetMapping("/{insectId}/photos")
    public ResponseEntity<List<InsectPhotoResponseDto>> getPhotos(@PathVariable Long insectId) {
        return ResponseEntity.ok(insectPhotoService.getPhotos(insectId));
    }

    @GetMapping("/{insectId}/photos/{photoId}")
    public ResponseEntity<InsectPhotoResponseDto> getPhoto(@PathVariable Long insectId,
            @PathVariable Long photoId) {
        return ResponseEntity.ok(insectPhotoService.getPhoto(insectId, photoId));
    }

    @DeleteMapping("/{insectId}/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long insectId,
            @PathVariable Long photoId) {
        insectPhotoService.deletePhoto(user.getId(), insectId, photoId);

        return ResponseEntity.ok().build();
    }

}
