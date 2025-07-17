package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.ModCommentRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.ModCommentResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.ModCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ModCommentService modCommentService;

    @PostMapping("/mods/{gameModId}")
    public ResponseEntity<ApiResponse<ModCommentResponse>> createModComment(
            @PathVariable Long gameModId,
            @Valid @RequestBody ModCommentRequest request,
            @AuthenticationPrincipal User currentUser) {

        ModCommentResponse response = modCommentService.createComment(gameModId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment created successfully", response));
    }

    @GetMapping("/mods/{gameModId}")
    public ResponseEntity<ApiResponse<List<ModCommentResponse>>> getModComments(
            @PathVariable Long gameModId) {

        List<ModCommentResponse> comments = modCommentService.getCommentsByModId(gameModId);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
}