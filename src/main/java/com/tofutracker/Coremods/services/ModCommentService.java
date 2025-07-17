package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.enums.Permission;
import com.tofutracker.Coremods.dto.requests.CommentUpdateRequest;
import com.tofutracker.Coremods.dto.requests.ModCommentRequest;
import com.tofutracker.Coremods.dto.responses.ModCommentResponse;
import com.tofutracker.Coremods.entity.Comment;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.CommentRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Transactional
    public ModCommentResponse updateComment(Long commentId, CommentUpdateRequest request, User user) {
        validateUserForOperation(user);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!Objects.equals(comment.getUser().getId(), user.getId())) {
            throw new ForbiddenException("You can only update your own comments");
        }

        if (comment.isDeleted()) {
            throw new ForbiddenException("Cannot update a deleted comment");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return mapToResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        validateUserForOperation(user);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isAuthor = Objects.equals(comment.getUser().getId(), user.getId());
        boolean canDelete = user.getAuthorities()
                .contains(new SimpleGrantedAuthority(Permission.COMMENT_DELETE.getPermission()));

        if (!isAuthor && !canDelete) {
            throw new ForbiddenException("You don't have permission to delete this comment");
        }

        if (comment.isDeleted()) {
            throw new ForbiddenException("Comment is already deleted");
        }

        comment.softDelete();
        commentRepository.save(comment);
    }

    public List<ModCommentResponse> getCommentsByModId(Long gameModId) {
        List<Comment> comments = commentRepository.findByCommentableTypeAndCommentableIdOrderByCreatedAtAsc("mod",
                gameModId);
        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateUserForOperation(User user) {
        log.info("user: {}", user);
        if (user == null) {
            throw new UnauthorizedException("User must be authenticated");
        }

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("User must have verified email.");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is disabled.");
        }

        if (!user.isAccountNonExpired()) {
            throw new UnauthorizedException("User account is expired.");
        }

        if (!user.isAccountNonLocked()) {
            throw new UnauthorizedException("User account is locked.");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new UnauthorizedException("User credentials are expired.");
        }
    }

    private ModCommentResponse mapToResponse(Comment comment) {
        boolean isDeleted = comment.isDeleted();
        Comment parent = comment.getParent();

        return ModCommentResponse.builder()
                .id(comment.getId())
                .content(isDeleted ? null : comment.getContent())
                .username(isDeleted ? null : comment.getUser().getUsername())
                .parentId(parent != null ? parent.getId() : null)
                .isDeleted(isDeleted)
                .isUpdated(isDeleted ? false : comment.isUpdated())
                .createdAt(comment.getCreatedAt())
                .updatedAt(isDeleted ? null : comment.getUpdatedAt())
                .build();
    }
}