package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.dto.requests.ModCommentRequest;
import com.tofutracker.Coremods.dto.responses.ModCommentResponse;
import com.tofutracker.Coremods.entity.Comment;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.CommentRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModCommentService {

    private final CommentRepository commentRepository;
    private final GameModRepository gameModRepository;

    @Transactional
    public ModCommentResponse createComment(Long gameModId, ModCommentRequest request, User user) {
        validateUserForOperation(user);

        gameModRepository.findById(gameModId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + gameModId));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent comment not found with id: " + request.getParentId()));
        }

        Comment comment = Comment.builder()
                .commentableType("mod")
                .commentableId(gameModId)
                .user(user)
                .content(request.getContent())
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    public List<ModCommentResponse> getCommentsByModId(Long gameModId) {
        List<Comment> comments = commentRepository.findByCommentableTypeAndCommentableIdOrderByCreatedAtAsc("mod",
                gameModId);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateUserForOperation(User user) {
        if (user == null) {
            throw new UnauthorizedException("User must be authenticated to comment");
        }

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("User must have verified email to comment");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is disabled");
        }

        if (!user.isAccountNonExpired()) {
            throw new UnauthorizedException("User account is expired");
        }

        if (!user.isAccountNonLocked()) {
            throw new UnauthorizedException("User account is locked");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new UnauthorizedException("User credentials are expired");
        }
    }

    private ModCommentResponse mapToResponse(Comment comment) {
        return ModCommentResponse.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? null : comment.getContent())
                .username(comment.isDeleted() ? null : comment.getUser().getUsername())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .isDeleted(comment.isDeleted())
                .isUpdated(comment.isUpdated())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}