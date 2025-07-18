package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.tags.CreateModTagResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModTag;
import com.tofutracker.Coremods.entity.ModTagVote;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModTagRepository;
import com.tofutracker.Coremods.repository.ModTagVoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModTagService {

    private final ModTagRepository modTagRepository;
    private final GameModRepository gameModRepository;
    private final ModTagVoteRepository modTagVoteRepository;

    @Transactional
    public ResponseEntity<ApiResponse<CreateModTagResponse>> createTag(Long modId, String tag, User user) {
        validateUserForOperation(user);

        GameMod mod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        String normalizedTag = normalizeTag(tag);

        validateTag(normalizedTag);

        Optional<ModTag> existingTag = modTagRepository.findByModIdAndTag(modId, normalizedTag);

        if (existingTag.isPresent()) {
            throw new BadRequestException("Tag '" + normalizedTag + "' already exists for this mod");
        }

        ModTag modTag = ModTag.builder()
                .mod(mod)
                .tag(normalizedTag)
                .user(user)
                .build();

        modTagRepository.save(modTag);

        CreateModTagResponse response = CreateModTagResponse.fromEntity(modTag);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tag created successfully", response));
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> voteForModTag(Long modId, Long tagId, User user) {
        validateUserForOperation(user);

        gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod not found with id: " + modId));

        ModTag modTag = modTagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag not found with id: " + tagId + " for mod with id: " + modId));

        Optional<ModTagVote> existingVote = modTagVoteRepository.findByModTagIdAndUserId(tagId, user.getId());

        if (existingVote.isPresent()) {
            throw new BadRequestException("You have already voted for this tag");
        }

        ModTagVote modTagVote = ModTagVote.builder()
                .modTag(modTag)
                .user(user)
                .build();

        modTagVoteRepository.save(modTagVote);

        return ResponseEntity.ok(ApiResponse.success("Tag voted successfully"));
    }

    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteVoteForTag(Long modId, Long tagId, User user) {
        validateUserForOperation(user);

        gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod not found with id: " + modId));

        modTagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tag not found with id: " + tagId + " for mod with id: " + modId));

        ModTagVote modTagVote = modTagVoteRepository.findByModTagIdAndUserId(tagId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vote not found for tag with id: " + tagId + " and user with id: " + user.getId()));

        modTagVoteRepository.delete(modTagVote);

        return ResponseEntity.ok(ApiResponse.success("Tag unvoted successfully"));
    }

    private String normalizeTag(String tag) {
        return tag.trim();
    }

    private void validateTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            throw new BadRequestException("Tag cannot be empty");
        }

        if (tag.length() < 2) {
            throw new BadRequestException("Tag must be at least 2 characters");
        }

        if (tag.length() > 50) {
            throw new BadRequestException("Tag cannot exceed 50 characters");
        }

        // Check for valid characters (alphanumeric, hyphens)
        if (!tag.matches("^[a-zA-Z0-9-]+$")) {
            throw new BadRequestException("Tag can only contain letters, numbers, and hyphens");
        }
    }

    private void validateUserForOperation(User user) {
        if (user == null) {
            throw new UnauthorizedException("User must be authenticated");
        }

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("User must have verified email");
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
}