package com.tofutracker.Coremods.services.mods;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import com.tofutracker.Coremods.dto.responses.mods.ModFileResponse;
import com.tofutracker.Coremods.dto.responses.mods.ModFileUploadResponse;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModFile;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModFileRepository;
import com.tofutracker.Coremods.services.ArchiveService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public ModFileResponse getModFile(Long id) {
        ModFile modFile = modFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mod file not found with ID: " + id));

        String fileUrl = modFileStorageService.getModFileUrl(modFile.getStorageKey(), modFile.getCategory());

        return ModFileResponse.fromEntity(modFile, fileUrl);
    }
    
    @Transactional(readOnly = true)
    public List<ModFileResponse> getModFilesByMod(Long modId) {
        GameMod mod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod not found with ID: " + modId));
        
        List<ModFile> modFiles = modFileRepository.findByMod(mod);
        
        return modFiles.stream()
                .map(modFile -> {
                    String fileUrl = modFileStorageService.getModFileUrl(modFile.getStorageKey(), modFile.getCategory());
                    return ModFileResponse.fromEntity(modFile, fileUrl);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ModFileResponse> getModFilesByModAndCategory(Long modId, FileCategory category) {
        GameMod mod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Mod not found with ID: " + modId));
        
        List<ModFile> modFiles = modFileRepository.findByModAndCategory(mod, category);
        
        return modFiles.stream()
                .map(modFile -> {
                    String fileUrl = modFileStorageService.getModFileUrl(modFile.getStorageKey(), modFile.getCategory());
                    return ModFileResponse.fromEntity(modFile, fileUrl);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void archiveModFile(Long modId, Long id, User currentUser) {

        GameMod gameMod = gameModRepository.findById(modId)
                .orElseThrow(()  -> new ResourceNotFoundException("Mod not found with ID: " + modId));

        ModFile modFile = modFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mod file not found with ID: " + id));


        if (!gameMod.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to archive this file");
        }
        
        try {
            String archivedKey = modFileStorageService.moveFileToArchive(
                    modFile.getStorageKey(), 
                    modFile.getMod(), 
                    modFile.getName(), 
                    modFile.getExt());

            modFile.setStorageKey(archivedKey);
            modFile.setCategory(FileCategory.ARCHIVE);
            modFileRepository.save(modFile);
            
            log.info("File with ID {} has been archived", modFile.getId());
        } catch (IOException e) {
            log.error("Failed to archive file with ID {}: {}", modFile.getId(), e.getMessage());
            throw new RuntimeException("Failed to archive file", e);
        }
    }
    
    @Transactional
    public ModFileResponse incrementDownloadCount(Long id) {
        ModFile modFile = modFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mod file not found with ID: " + id));
        
        modFile.setDownloadCount(modFile.getDownloadCount() + 1);
        ModFile savedModFile = modFileRepository.save(modFile);
        
        String fileUrl = modFileStorageService.getModFileUrl(savedModFile.getStorageKey(), savedModFile.getCategory());
        
        return ModFileResponse.fromEntity(savedModFile, fileUrl);
    }
} 