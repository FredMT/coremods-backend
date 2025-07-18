package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.CreateModTagRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.CreateModTagResponse;
import com.tofutracker.Coremods.entity.ModTag;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.tags.ModTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/mods/{modId}/tags")
@RequiredArgsConstructor
public class ModTagController {

    private final ModTagService modTagService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateModTagResponse>> createTag(
            @PathVariable Long modId,
            @Valid @RequestBody CreateModTagRequest request,
            @AuthenticationPrincipal User currentUser) {

        ModTag createdTag = modTagService.createTag(modId, request.getTag(), currentUser);

        CreateModTagResponse response = CreateModTagResponse.fromEntity(createdTag);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag created successfully", response));
    }
}