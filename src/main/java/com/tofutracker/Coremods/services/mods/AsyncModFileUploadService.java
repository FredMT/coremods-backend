package com.tofutracker.Coremods.services.mods;

import com.amazonaws.event.ProgressListener;
import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.requests.mods.upload_files.ModFileUploadRequest;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.ModFile;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.ModFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncModFileUploadService {

    private final ModFileRepository modFileRepository;
    private final GameModRepository gameModRepository;
    private final ModFileStorageService modFileStorageService;
    
    @Lazy
    private final ModFileService modFileService;

    @Async("taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processFileUpload(Long modId, ModFileUploadRequest request, User currentUser, String progressId) {
        try {
            MultipartFile file = request.getArchiveFile();

            GameMod mod = gameModRepository.findById(modId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mod not found with ID: " + modId));

            if (!mod.getAuthor().getId().equals(currentUser.getId())) {
                modFileService.getUploadProgressMap().put(progressId, -1);
                log.error("Unauthorized upload attempt for mod {} by user {}", modId, currentUser.getId());
                return;
            }
            
            String fileName = request.getFileName();
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);

            final long[] totalBytesTransferred = {0L};
            final long totalFileSize = file.getSize();
            final boolean[] progressListenerCalled = {false};
            final int[] lastReportedProgress = {0};
            
            log.info("Starting upload for file: {} (size: {} bytes)", originalFilename, totalFileSize);

            ProgressListener progressListener = progressEvent -> {
                progressListenerCalled[0] = true;

                switch (progressEvent.getEventType()) {
                    case REQUEST_BYTE_TRANSFER_EVENT:
                    case RESPONSE_BYTE_TRANSFER_EVENT:
                        long eventBytes = progressEvent.getBytesTransferred();
                        if (eventBytes > 0) {
                            totalBytesTransferred[0] += eventBytes;

                            int percentComplete = (int) ((totalBytesTransferred[0] * 100) / totalFileSize);
                            percentComplete = Math.min(percentComplete, 100);

                            int progressDifference = percentComplete - lastReportedProgress[0];
                            if (progressDifference >= 15 || percentComplete == 100) {
                                modFileService.getUploadProgressMap().put(progressId, percentComplete);
                                lastReportedProgress[0] = percentComplete;
                            }
                        }
                        break;
                    case TRANSFER_STARTED_EVENT:
                        log.info("Transfer started for progressId: {}", progressId);
                        modFileService.getUploadProgressMap().put(progressId, 1);
                        lastReportedProgress[0] = 1;
                        break;
                    case TRANSFER_COMPLETED_EVENT:
                        log.info("Transfer completed for progressId: {}", progressId);
                        modFileService.getUploadProgressMap().put(progressId, 100);
                        lastReportedProgress[0] = 100;
                        break;
                    case TRANSFER_FAILED_EVENT:
                        log.error("Transfer failed for progressId: {}", progressId);
                        modFileService.getUploadProgressMap().put(progressId, -1);
                        break;
                    default:
                        log.debug("Other progress event for {}: {}", progressId, progressEvent.getEventType());
                        break;
                }
            };

            String storageKey = modFileStorageService.uploadModFile(file, mod, request.getFileCategory(), fileName, progressListener);
            log.info("File uploaded to S3 for modId: {}, key: {}", mod.getId(), storageKey);

            if (!progressListenerCalled[0]) {
                log.warn("Progress listener was never called for progressId: {}. This might indicate an issue with AWS SDK progress tracking.", progressId);
            }

            modFileService.getUploadProgressMap().put(progressId, 100);
            
            ModFile modFile = ModFile.builder()
                    .mod(mod)
                    .name(fileName)
                    .originalFilename(originalFilename)
                    .storageKey(storageKey)
                    .size(file.getSize())
                    .ext(extension)
                    .description(request.getFileDescription())
                    .version(request.getFileVersion())
                    .category(request.getFileCategory())
                    .downloadCount(0L)
                    .build();

            if (Boolean.TRUE.equals(request.getIsNewVersionOfExistingFile()) && request.getFileId() != null) {
                ModFile existingFile = modFileRepository.findById(request.getFileId())
                        .orElseThrow(() -> new ResourceNotFoundException("Existing file not found with ID: " + request.getFileId()));

                if (!existingFile.getMod().getAuthor().getId().equals(currentUser.getId())) {
                    throw new ForbiddenException("You are not authorized to update this file");
                }

                if (Boolean.TRUE.equals(request.getRemovePreviousFileVersion())) {
                    String archivedKey = modFileStorageService.moveFileToArchive(
                            existingFile.getStorageKey(), 
                            mod, 
                            existingFile.getName(), 
                            existingFile.getExt());

                    existingFile.setStorageKey(archivedKey);
                    existingFile.setCategory(FileCategory.ARCHIVE);
                    modFileRepository.save(existingFile);
                    
                    log.info("File with ID {} has been archived", existingFile.getId());
                }
            }

            ModFile savedModFile = modFileRepository.save(modFile);

            modFileService.getUploadProgressMap().put(progressId, 100);
            scheduleProgressCleanup(progressId);
            log.info("File upload completed successfully for progressId: {}, fileId: {}", progressId, savedModFile.getId());
            
        } catch (Exception e) {
            modFileService.getUploadProgressMap().put(progressId, -1);
            scheduleProgressCleanup(progressId);
            log.error("File upload failed for progressId: {}", progressId, e);
        }
    }
    
    private void scheduleProgressCleanup(String progressId) {
        modFileService.getScheduler().schedule(() -> {
            modFileService.getUploadProgressMap().remove(progressId);
            log.debug("Removed progress tracking for ID: {}", progressId);
        }, 2, TimeUnit.SECONDS);
    }
} 