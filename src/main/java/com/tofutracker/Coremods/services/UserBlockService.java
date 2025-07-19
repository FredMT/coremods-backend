package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.enums.BlockScopeType;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.entity.UserBlock;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ImATeapotException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.UserBlockRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;

    public ResponseEntity<ApiResponse<Void>> blockUser(User targetUser, User currentUser,
            BlockScopeType scopeType, GameMod mod) {
        validateBlockRequest(targetUser, currentUser, scopeType, mod);

        String targetUserUsername = targetUser.getUsername();

        boolean alreadyBlocked = userBlockRepository
                .findByBlockerIdAndBlockedIdAndScopeType(currentUser.getId(), targetUser.getId(), scopeType)
                .isPresent();

        if (alreadyBlocked) {
            throw new BadRequestException(
                    targetUserUsername + " is already blocked from " + scopeDescription(scopeType));
        }

        UserBlock userBlock = UserBlock.builder()
                .blocker(currentUser)
                .blocked(targetUser)
                .scopeType(scopeType)
                .mod(mod)
                .build();

        userBlockRepository.save(userBlock);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse
                        .success(targetUserUsername + " has been blocked from " + scopeDescription(scopeType) + "."));
    }

    public ResponseEntity<ApiResponse<Void>> unblockUser(User targetUser, User currentUser,
            BlockScopeType scopeType, GameMod mod) {

        if (targetUser.getId().equals(currentUser.getId())) {
            throw new ImATeapotException(getSelfBlockMessage(scopeType));
        }

        Optional<UserBlock> block;

        if (scopeType == BlockScopeType.MOD && mod != null) {
            block = userBlockRepository.findByBlockerIdAndBlockedIdAndScopeTypeAndModId(
                    currentUser.getId(), targetUser.getId(), scopeType, mod.getId());
        } else {
            block = userBlockRepository.findByBlockerIdAndBlockedIdAndScopeType(
                    currentUser.getId(), targetUser.getId(), scopeType);
        }

        if (block.isEmpty()) {
            throw new ResourceNotFoundException(
                    targetUser.getUsername() + " is not blocked from " + scopeDescription(scopeType));
        }

        userBlockRepository.delete(block.get());

        return ResponseEntity.ok(ApiResponse.success(
                targetUser.getUsername() + " has been unblocked from " + scopeDescription(scopeType) + "."));
    }

    private void validateBlockRequest(User targetUser, User currentUser, BlockScopeType scopeType, GameMod mod) {
        if (targetUser.getId().equals(currentUser.getId())) {
            throw new ImATeapotException(getSelfBlockMessage(scopeType));
        }

        if (scopeType == BlockScopeType.MOD) {
            if (mod == null) {
                throw new BadRequestException("You must specify a valid mod for mod-specific blocks");
            }

            if (!mod.getAuthor().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("You do not have permission to block a user from this mod");
            }
        }
    }

    private String getSelfBlockMessage(BlockScopeType scope) {
        return switch (scope) {
            case MOD -> "You can't block or unblock yourself from a mod";
            case AUTHOR_GLOBAL -> "You can't block or unblock yourself from your own mods.";
            case DIRECT_MESSAGES -> "You can't DM yourself.";
            case INTERACTION -> "You can't block or unblock yourself.";
        };
    }

    private String scopeDescription(BlockScopeType scope) {
        return switch (scope) {
            case MOD -> "this mod";
            case AUTHOR_GLOBAL -> "all your mods in the global scope";
            case DIRECT_MESSAGES -> "messaging you";
            case INTERACTION -> "interacting with you";
        };
    }
}
