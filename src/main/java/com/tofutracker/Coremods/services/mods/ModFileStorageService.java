package com.tofutracker.Coremods.services.mods;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.services.ArchiveConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModFileStorageService {

    private final AmazonS3 s3Client;

    @Value("${do.space.bucket}")
    private String doSpaceBucket;

    @Value("${do.space.folder:mods/files/}")
    private String modFilesBaseFolder;


    public String uploadModFile(MultipartFile file, GameMod mod, FileCategory fileCategory, String fileName, ProgressListener progressListener) throws IOException {
        if (file.getSize() > ArchiveConstants.MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of " + 
                    (ArchiveConstants.MAX_TOTAL_SIZE / 1_000_000) + " MB");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String folderPath = buildFolderPath(mod, fileCategory);
        String key = folderPath + fileName + "." + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                doSpaceBucket, 
                key, 
                file.getInputStream(), 
                metadata
        );

        if (fileCategory != FileCategory.ARCHIVE) {
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        }

        if (progressListener != null) {
            putObjectRequest.withGeneralProgressListener(progressListener);
            log.info("Progress listener attached to upload request for key: {}", key);
        } else {
            log.warn("No progress listener provided for upload key: {}", key);
        }

        s3Client.putObject(putObjectRequest);
        log.info("Mod file uploaded successfully to S3: {}", key);
        return key;
    }

    public void deleteModFile(String key) {
        s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
        log.info("Mod file deleted from S3: {}", key);
    }

    public String moveFileToArchive(String sourceKey, GameMod mod, String fileName, String extension) throws IOException {
        String archiveFolderPath = buildFolderPath(mod, FileCategory.ARCHIVE);
        String destinationKey = archiveFolderPath + fileName + "." + extension;

        s3Client.copyObject(doSpaceBucket, sourceKey, doSpaceBucket, destinationKey);
        log.info("File copied from {} to {}", sourceKey, destinationKey);

        deleteModFile(sourceKey);
        
        return destinationKey;
    }

    public String getModFileUrl(String key, FileCategory fileCategory) {
        if (fileCategory == FileCategory.ARCHIVE) {
            // For archived files, return a pre-signed URL with temporary access
            return s3Client.generatePresignedUrl(doSpaceBucket, key, 
                    java.util.Date.from(java.time.Instant.now().plusSeconds(3600))).toString();
        } else {
            // For public files, return the direct URL
            return s3Client.getUrl(doSpaceBucket, key).toString();
        }
    }

    private String buildFolderPath(GameMod mod, FileCategory fileCategory) {
        return modFilesBaseFolder + mod.getId() + "/" + fileCategory.getDisplayName().replace(" ", "_").toLowerCase() + "/";
    }
} 