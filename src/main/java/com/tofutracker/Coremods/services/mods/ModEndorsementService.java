package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModEndorsement;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModEndorsementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModEndorsementService {
    private final GameModRepository gameModRepository;
    private final ModEndorsementRepository modEndorsementRepository;

    public void endorseMod(Long modId, User currentUser) {

        GameMod gameMod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        Optional<ModEndorsement> modEndorsement = modEndorsementRepository.findByModIdAndUserId(modId,
                currentUser.getId());

        if (modEndorsement.isPresent()) {
            throw new BadRequestException("You already endorsed this mod.");
        }

        ModEndorsement newModEndorsement = ModEndorsement.builder().mod(gameMod).user(currentUser).build();

        modEndorsementRepository.save(newModEndorsement);
    }

    public void removeEndorsement(Long modId, User currentUser) {

        gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        Optional<ModEndorsement> modEndorsement = modEndorsementRepository.findByModIdAndUserId(modId,
                currentUser.getId());

        if (modEndorsement.isEmpty()) {
            throw new BadRequestException("You have not endorsed this mod.");
        }

        modEndorsementRepository.delete(modEndorsement.get());
    }
}
