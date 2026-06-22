package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.model.bean.InsectBean;
import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.service.InsectService;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insects")
public class InsectController {
    private Logger LOG = LoggerFactory.getLogger(InsectController.class);
    private final InsectService insectService;

    public InsectController(InsectService insectService) {
        this.insectService = insectService;
    }

    @GetMapping
    public ResponseEntity<List<InsectDto>> getInsects() {
        return ResponseEntity.ok(insectService.getInsects().stream().map(InsectBean::toDto).toList());
    }

    @GetMapping("/{insectId}")
    public ResponseEntity<InsectDto> getInsect(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long insectId) {
        return ResponseEntity.ok(InsectBean.toDto(insectService.getInsect(user.getId(), insectId)));
    }

    @PostMapping
    public ResponseEntity<Void> createInsect(@AuthenticationPrincipal CustomUserDetails user,
            @RequestBody() @Valid InsectCreateDto dto) {
        insectService.createInsect(user.getId(),
                new InsectBean(dto.getName(), dto.getDescription()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateInsect(@AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid InsectUpdateDto dto) {
        insectService.updateInsect(user.getId(),
                new InsectBean(dto.getInsectId(), dto.getName(), dto.getDescription()));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<InsectDto>> getInsects(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(insectService.getInsectsForUserId(user.getId()).stream()
                .map(InsectBean::toDto)
                .toList());
    }

    @DeleteMapping("/{insectId}")
    public ResponseEntity<Void> deleteInsect(@AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long insectId) {
        insectService.deleteInsect(user.getId(), insectId);

        return ResponseEntity.ok().build();
    }

}
