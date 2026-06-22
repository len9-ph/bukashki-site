package com.lgadetsky.bukashki.controller;

import com.lgadetsky.bukashki.model.bean.InsectBean;
import com.lgadetsky.bukashki.model.dto.InsectCreateDto;
import com.lgadetsky.bukashki.model.dto.InsectDto;
import com.lgadetsky.bukashki.model.dto.InsectUpdateDto;
import com.lgadetsky.bukashki.security.CustomUserDetails;
import com.lgadetsky.bukashki.service.InsectService;
import com.lgadetsky.bukashki.service.impl.InsectServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insect")
public class InsectController {
    private Logger LOG = LoggerFactory.getLogger(InsectController.class);
    private final InsectService insectService;

    public InsectController(InsectService insectService, InsectServiceImpl insectServiceImpl) {
        this.insectService = insectService;
    }

    @GetMapping("/{insectId}")
    public ResponseEntity<InsectDto> getInsect(@PathVariable Long insectId) {
        LOG.debug("get insect for id = {}", insectId);

        return ResponseEntity.ok(InsectBean.toDto(insectService.getInsect(insectId)));
    }

    @PostMapping
    public ResponseEntity<Void> createInsect(Authentication authentication, @RequestBody() @Valid InsectCreateDto dto) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currUserId = userDetails.getId();

        insectService.createInsect(currUserId,
                new InsectBean(dto.getName(), dto.getDescription()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateInsect(Authentication authentication, @RequestBody() @Valid InsectUpdateDto dto) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currUserId = userDetails.getId();

        insectService.updateInsect(currUserId,
                new InsectBean(dto.getInsectId(), dto.getName(), dto.getDescription()));

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<InsectDto>> getInsects(@RequestParam Long userId) {
        return ResponseEntity.ok(insectService.getInsectsForUserId(userId).stream()
                .map(InsectBean::toDto)
                .toList());
    }

    @DeleteMapping("/{insectId}")
    public ResponseEntity<Void> deleteInsect(Authentication authentication, @PathVariable Long insectId) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currUserId = userDetails.getId();

        insectService.deleteInsect(currUserId, insectId);

        return ResponseEntity.ok().build();
    }

}
