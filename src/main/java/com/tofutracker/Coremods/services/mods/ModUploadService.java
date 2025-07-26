package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModDetailsRequest;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModPermissions;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModFileRepository;
import com.tofutracker.Coremods.repository.ModPermissionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModUploadService {

    private final ModDetailsService modDetailsService;
    private final GameModRepository gameModRepository;
    private final ModFileRepository modFileRepository;
    private final ModPermissionsRepository modPermissionsRepository;
    
    @Transactional
    public void saveModDetails(ModDetailsRequest modDetailsRequest, User currentUser) {

        log.info("Saving mod details for user: {}, mod: {}",
                currentUser.getUsername(), modDetailsRequest.getName());

        modDetailsService.saveModDraft(modDetailsRequest, currentUser);
    }

    public void publishMod(Long gameModId, User currentUser) {
        GameMod gameMod = gameModRepository.findById(gameModId).orElseThrow(
                () -> new ResourceNotFoundException("Game mod not found with ID: " + gameModId)
        );

        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to publish this mod.");
        }

        if (gameMod.getModType() == null ||
            gameMod.getCategory() == null ||
            gameMod.getName() == null || gameMod.getName().isBlank() ||
            gameMod.getVersion() == null || gameMod.getVersion().isBlank() ||
            gameMod.getDescription() == null || gameMod.getDescription().isBlank()) {
            throw new BadRequestException("Mod is missing required details (mod type, category, name, version, author, or description)");
        }

        if (modFileRepository.findByMod(gameMod).isEmpty()) {
            throw new BadRequestException("At least one file must be uploaded before publishing the mod");
        }

        Optional<ModPermissions> permissionsOpt = modPermissionsRepository.findByModIdAndIsLatestTrue(gameMod.getId());
        if (permissionsOpt.isEmpty()) {
            throw new BadRequestException("Mod permissions must be set before publishing");
        }

        gameMod.setStatus(GameMod.ModStatus.PUBLISHED);
        gameMod.setPublished(true);
        gameModRepository.save(gameMod);
    }
}