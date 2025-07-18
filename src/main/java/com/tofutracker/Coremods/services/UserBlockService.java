package com.tofutracker.Coremods.services;

import com.tofutracker.Coremods.config.enums.BlockScopeType;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.entity.UserBlock;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ImATeapotException;
import com.tofutracker.Coremods.repository.UserBlockRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBlockService {
    private final UserBlockRepository userBlockRepository;

    public ResponseEntity<ApiResponse<Void>> blockUserFromMod(User targetUser, GameMod gameMod, User currentUser) {

        Optional<UserBlock> block = userBlockRepository.findByBlockerIdAndBlockedIdAndScopeType(currentUser.getId(),
                targetUser.getId(), BlockScopeType.MOD);

        if (block.isPresent()) {
            throw new BadRequestException(targetUser.getUsername() + " is already blocked from this mod");
        }

        if (!currentUser.getId().equals(gameMod.getAuthor().getId())) {
            throw new ForbiddenException("You do not have permission to block a user from this mod");
        }

        if (targetUser.getId().equals(currentUser.getId())) {
            throw new ImATeapotException("You can't block yourself from a mod");
        }

        UserBlock userBlock = blockUserFromModEntityBuilder(targetUser, gameMod, currentUser);

        userBlockRepository.save(userBlock);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(targetUser.getUsername() + " has been blocked from this mod."));
    }

    private UserBlock blockUserFromModEntityBuilder(User targetUser, GameMod gameMod, User currentUser) {
        return UserBlock.builder()
                .blocker(currentUser)
                .blocked(targetUser)
                .mod(gameMod)
                .scopeType(BlockScopeType.MOD)
                .build();
    }

}
