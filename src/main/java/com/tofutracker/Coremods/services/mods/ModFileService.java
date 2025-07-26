package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.requests.mods.ModFileEditRequest;
import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import com.tofutracker.Coremods.dto.responses.mods.ModFileEditResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileUploadResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModFile;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModFileRepository;
import com.tofutracker.Coremods.services.archive_validation.ArchiveService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@Slf4j
public class ModFileService {

    private final ModFileRepository modFileRepository;
    private final GameModRepository gameModRepository;
    private final ModFileStorageService modFileStorageService;
    private final ArchiveService archiveService;
    private final AsyncModFileUploadService asyncModFileUploadService;

    @Getter
    private final ConcurrentHashMap<String, Integer> uploadProgressMap = new ConcurrentHashMap<>();

    @Getter
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ModFileService(
            ModFileRepository modFileRepository,
            GameModRepository gameModRepository,
            ModFileStorageService modFileStorageService,
            ArchiveService archiveService,
            @Lazy AsyncModFileUploadService asyncModFileUploadService) {
        this.modFileRepository = modFileRepository;
        this.gameModRepository = gameModRepository;
        this.modFileStorageService = modFileStorageService;
        this.archiveService = archiveService;
        this.asyncModFileUploadService = asyncModFileUploadService;
    }

    @Transactional
    public ModFileUploadResponse startModFileUpload(Long modId, ModFileUploadRequest request, User currentUser) throws IOException {
        log.info("Starting file upload validation for mod ID: {}", modId);
        
        GameMod mod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod not found with ID: " + modId));

        if (!mod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to upload files for this mod");
        }

        Map<String, Object> validationResult = archiveService.validateArchive(request.getArchiveFile());
        log.info("Archive validation completed for mod ID: {}", modId);

        final String progressId = UUID.randomUUID().toString().replace("-", "");

        uploadProgressMap.put(progressId, 0);

        log.info("Starting async upload for progressId: {}", progressId);

        asyncModFileUploadService.processFileUpload(modId, request, currentUser, progressId);

        return ModFileUploadResponse.builder()
                .progressId(progressId)
                .validationResult(validationResult)
                .status("STARTED")
                .build();
    }

    public int getUploadProgress(String progressId) {
        return uploadProgressMap.getOrDefault(progressId, -1);
    }

//    @Transactional(readOnly = true)
//    public ModFileResponse getModFile(Long id) {
//        ModFile modFile = modFileRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Mod file not found with ID: " + id));
//
//        String fileUrl = modFileStorageService.getModFileUrl(modFile.getStorageKey(), modFile.getCategory());
//
//        return ModFileResponse.fromEntity(modFile, fileUrl);
//    }

    @Transactional
    public ModFileEditResponse editModFile(Long modId, Long fileId, ModFileEditRequest request, User currentUser) {
        ModFile modFile = modFileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod file not found with ID: " + fileId));

        if (!modFile.getMod().getId().equals(modId)) {
            throw new BadRequestException("File does not belong to the specified mod");
        }

        if (!modFile.getMod().getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to edit this mod file");
        }

        boolean hasChanges = false;

        hasChanges |= updateIfChanged(modFile::getName, modFile::setName, request.getName());
        hasChanges |= updateIfChanged(modFile::getVersion, modFile::setVersion, request.getVersion());
        hasChanges |= updateIfChanged(modFile::getDescription, modFile::setDescription, request.getDescription());

        if (request.getCategory() != null) {
            FileCategory newCategory = FileCategory.valueOf(request.getCategory());
            if (!newCategory.equals(modFile.getCategory())) {
                String oldStorageKey = modFile.getStorageKey();
                String newStorageKey = moveFileToNewCategory(modFile, newCategory);
                modFile.setStorageKey(newStorageKey);
                modFile.setCategory(newCategory);
                hasChanges = true;
                log.info("File moved from {} to {} due to category change", oldStorageKey, newStorageKey);
            }
        }

        if (!hasChanges) {
            throw new BadRequestException("At least one field must be provided and different from current values");
        }

        ModFile savedModFile = modFileRepository.save(modFile);
        log.info("Mod file updated successfully: {}", savedModFile.getId());

        return ModFileEditResponse.fromEntity(savedModFile);
    }

    private String moveFileToNewCategory(ModFile modFile, FileCategory newCategory) {
        String oldStorageKey = modFile.getStorageKey();
        String fileName = modFile.getName();
        String extension = modFile.getExt();
        GameMod mod = modFile.getMod();

        return modFileStorageService.moveFileToCategory(oldStorageKey, mod, newCategory, fileName, extension);
    }

    private <T> boolean updateIfChanged(Supplier<T> getter, Consumer<T> setter, T newValue) {
        if (newValue != null && !Objects.equals(getter.get(), newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
} 