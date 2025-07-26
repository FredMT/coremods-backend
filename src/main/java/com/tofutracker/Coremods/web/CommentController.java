package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.comments.ModCommentRequest;
import com.tofutracker.Coremods.dto.requests.mods.comments.ModCommentUpdateRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.comments.ModCommentResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.ModCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ModCommentService modCommentService;

    @GetMapping("/mods/{gameModId}")
    public ResponseEntity<ApiResponse<List<ModCommentResponse>>> getModComments(@PathVariable Long gameModId) {
        List<ModCommentResponse> comments = modCommentService.getCommentsByModId(gameModId);

        if (comments.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No comments found"));
        }

        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }

    @PostMapping("/mods/{gameModId}")
    public ResponseEntity<ApiResponse<ModCommentResponse>> createModComment(@PathVariable Long gameModId,
            @Valid @RequestBody ModCommentRequest request, @AuthenticationPrincipal User currentUser) {
        ModCommentResponse response = modCommentService.createComment(gameModId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment created successfully", response));
    }

    @PutMapping("/mods/{commentId}")
    public ResponseEntity<ApiResponse<ModCommentResponse>> updateComment(@PathVariable Long commentId,
            @Valid @RequestBody ModCommentUpdateRequest request, @AuthenticationPrincipal User currentUser) {

        ModCommentResponse response = modCommentService.updateComment(commentId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/mods/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {

        modCommentService.deleteComment(commentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully"));
    }
}