package com.tofutracker.Coremods.services.tags;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModTag;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModTagService {

    private final ModTagRepository modTagRepository;
    private final GameModRepository gameModRepository;

    @Transactional
    public ModTag createTag(Long modId, String tag, User user) {
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

        return modTag;
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