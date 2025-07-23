package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.dto.requests.mods.upload_mod.ModRequirementsMirrorsRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.entity.*;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModRequirementsMirrorsService {

    private final GameModRepository gameModRepository;
    private final IgdbGameDLCRepository igdbGameDLCRepository;
    private final ModRequiredDlcRepository modRequiredDlcRepository;
    private final ModRequiredModRepository modRequiredModRepository;
    private final ModExternalRequirementRepository modExternalRequirementRepository;
    private final ModMirrorRepository modMirrorRepository;

    @Transactional
    public ApiResponse<Void> saveModRequirementsMirrors(ModRequirementsMirrorsRequest request, GameMod gameMod,
            User currentUser) {
        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not the author of this mod");
        }

        validateRequest(request, gameMod);
        saveData(request, gameMod);

        return ApiResponse.success("Mod requirements and mirrors saved successfully");
    }

    private void validateRequest(ModRequirementsMirrorsRequest request, GameMod gameMod) {
        IgdbGame game = gameMod.getGame();

        List<Long> dlcIds = request.getDlcIds();

        if (dlcIds != null && !dlcIds.isEmpty()) {
            validateDlcIds(dlcIds, game);
        }

        List<ModRequirementsMirrorsRequest.SiteModRequirement> siteMods = request.getSiteMods();

        if (siteMods != null && !siteMods.isEmpty()) {
            var modIds = siteMods.stream()
                    .map(ModRequirementsMirrorsRequest.SiteModRequirement::getModId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (!modIds.isEmpty()) {
                validateModIds(modIds);
            }
        }
    }

    private void saveData(ModRequirementsMirrorsRequest request, GameMod gameMod) {
        if (request.getDlcIds() != null && !request.getDlcIds().isEmpty()) {
            saveDlcRequirements(request.getDlcIds(), gameMod);
        }

        if (request.getSiteMods() != null && !request.getSiteMods().isEmpty()) {
            saveModRequirements(request.getSiteMods(), gameMod);
        }

        if (request.getExternalRequirements() != null && !request.getExternalRequirements().isEmpty()) {
            saveExternalRequirements(request.getExternalRequirements(), gameMod);
        }

        if (request.getModMirrors() != null && !request.getModMirrors().isEmpty()) {
            saveModMirrors(request.getModMirrors(), gameMod);
        }
    }

    private void saveDlcRequirements(List<Long> dlcIds, GameMod gameMod) {
        List<ModRequiredDlc> dlcRequirements = dlcIds.stream()
                .map(dlcId -> ModRequiredDlc.builder()
                        .mod(gameMod)
                        .dlcId(dlcId)
                        .build())
                .collect(Collectors.toList());
        modRequiredDlcRepository.saveAll(dlcRequirements);
    }

    private void saveModRequirements(List<ModRequirementsMirrorsRequest.SiteModRequirement> siteMods, GameMod gameMod) {
        List<ModRequiredMod> modRequirements = siteMods.stream()
                .filter(siteModReq -> siteModReq.getModId() != null)
                .map(siteModReq -> {
                    GameMod requiredMod = gameModRepository.findById(siteModReq.getModId())
                            .orElseThrow(
                                    () -> new BadRequestException("Required mod not found: " + siteModReq.getModId()));

                    return ModRequiredMod.builder()
                            .mod(gameMod)
                            .requiredMod(requiredMod)
                            .requirementNotes(siteModReq.getRequirementNotes())
                            .build();
                })
                .collect(Collectors.toList());
        modRequiredModRepository.saveAll(modRequirements);
    }

    private void saveExternalRequirements(List<ModRequirementsMirrorsRequest.ExternalRequirement> externalRequirements,
            GameMod gameMod) {
        List<ModExternalRequirement> requirements = externalRequirements.stream()
                .map(extReq -> ModExternalRequirement.builder()
                        .mod(gameMod)
                        .name(extReq.getName())
                        .url(extReq.getUrl())
                        .notes(extReq.getNotes())
                        .build())
                .collect(Collectors.toList());
        modExternalRequirementRepository.saveAll(requirements);
    }

    private void saveModMirrors(List<ModRequirementsMirrorsRequest.ModMirror> modMirrors, GameMod gameMod) {
        List<ModMirror> mirrors = modMirrors.stream()
                .map(mirror -> ModMirror.builder()
                        .mod(gameMod)
                        .mirrorName(mirror.getMirrorName())
                        .mirrorUrl(mirror.getMirrorUrl())
                        .build())
                .collect(Collectors.toList());
        modMirrorRepository.saveAll(mirrors);
    }

    private void validateDlcIds(List<Long> dlcIds, IgdbGame game) {
        Set<Long> validDlcIds = igdbGameDLCRepository.findValidDlcIdsByParentGameIdAndDlcIds(game.getId(), dlcIds);

        if (validDlcIds.size() != dlcIds.size()) {
            List<Long> invalidDlcIds = dlcIds.stream()
                    .filter(dlcId -> !validDlcIds.contains(dlcId))
                    .toList();
            throw new BadRequestException("Invalid DLC IDs for this game: " + invalidDlcIds);
        }
    }

    private void validateModIds(List<Long> modIds) {
        Set<Long> existingModIds = gameModRepository.findExistingModIds(modIds);

        if (existingModIds.size() != modIds.size()) {
            List<Long> invalidModIds = modIds.stream()
                    .filter(modId -> !existingModIds.contains(modId))
                    .toList();
            throw new BadRequestException("Invalid mod IDs: " + invalidModIds);
        }
    }
}