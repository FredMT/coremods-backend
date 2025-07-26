package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.tags.CreateModTagRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.tags.CreateModTagResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.mods.ModTagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mods/{modId}/tags")
@RequiredArgsConstructor
public class ModTagController {

    private final ModTagService modTagService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateModTagResponse>> createTag(
            @PathVariable Long modId,
            @Valid @RequestBody CreateModTagRequest request,
            @AuthenticationPrincipal User currentUser) {

        CreateModTagResponse tags = modTagService.createTag(modId, request.getTag(), currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag created successfully", tags));
    }

    @PostMapping("/{tagId}/vote")
    public ResponseEntity<ApiResponse<Void>> voteForTag(@PathVariable Long modId, @PathVariable Long tagId,
            @AuthenticationPrincipal User currentUser) {

        modTagService.voteForModTag(modId, tagId, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag voted successfully"));
    }

    @DeleteMapping("/{tagId}/vote")
    public ResponseEntity<ApiResponse<Void>> deleteVoteForTag(@PathVariable Long modId, @PathVariable Long tagId,
            @AuthenticationPrincipal User currentUser) {

        modTagService.deleteVoteForTag(modId, tagId, currentUser);

        return ResponseEntity.ok(ApiResponse.success("Tag unvoted successfully"));
    }
}