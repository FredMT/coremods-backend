package com.tofutracker.Coremods.services.mods;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.services.archive_validation.ArchiveConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModFileStorageService {

    private final S3Client s3Client;
    private final S3TransferManager transferManager;
    private final S3Presigner s3Presigner;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${do.space.folder:mods/}")
    private String modFilesBaseFolder;

    public String uploadModFile(MultipartFile file, GameMod mod, FileCategory fileCategory, String fileName,
            TransferListener transferListener) throws IOException {
        if (file.getSize() > ArchiveConstants.MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed size of " +
                    (ArchiveConstants.MAX_TOTAL_SIZE / 1_000_000) + " MB");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String folderPath = buildFolderPath(mod, fileCategory);
        String sanitizedFileName = sanitizeFileName(fileName);
        String storageFileName = sanitizedFileName + "_" + UUID.randomUUID().toString().replace("-", "");
        String key = folderPath + storageFileName + "." + extension;

        if (key.startsWith(".") || key.contains("..")) {
            throw new IllegalArgumentException("Invalid storage key: keys cannot start with '.' or contain '..'");
        }

        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key);

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            putObjectRequestBuilder.contentType(contentType);
        }

        if (fileCategory != FileCategory.ARCHIVE) {
            putObjectRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ);
        }

        UploadRequest.Builder uploadRequestBuilder = UploadRequest.builder()
                .putObjectRequest(putObjectRequestBuilder.build())
                .requestBody(AsyncRequestBody.fromBytes(file.getBytes()));

        if (transferListener != null) {
            uploadRequestBuilder.addTransferListener(transferListener);
            log.info("Transfer listener attached to upload request for key: {}", key);
        } else {
            log.warn("No transfer listener provided for upload key: {}", key);
        }

        Upload upload = transferManager.upload(uploadRequestBuilder.build());
        upload.completionFuture().join();

        log.info("Mod file uploaded successfully to S3: {}", key);
        return key;
    }

    public void deleteModFile(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("Mod file deleted from S3: {}", key);
    }

    public String moveFileToArchive(String sourceKey, GameMod mod, String fileName, String extension)
            throws IOException {
        String archiveFolderPath = buildFolderPath(mod, FileCategory.ARCHIVE);
        String sanitizedFileName = sanitizeFileName(fileName);
        String storageFileName = sanitizedFileName + "_" + UUID.randomUUID().toString().replace("-", "");
        String destinationKey = archiveFolderPath + storageFileName + "." + extension;

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();

        s3Client.copyObject(copyRequest);
        log.info("File copied from {} to {}", sourceKey, destinationKey);

        deleteModFile(sourceKey);

        return destinationKey;
    }

    public String moveFileToCategory(String sourceKey, GameMod mod, FileCategory newCategory, String fileName,
            String extension) {
        String newFolderPath = buildFolderPath(mod, newCategory);
        String sanitizedFileName = sanitizeFileName(fileName);
        String storageFileName = sanitizedFileName + "_" + UUID.randomUUID().toString().replace("-", "");
        String destinationKey = newFolderPath + storageFileName + "." + extension;

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(sourceKey)
                .destinationBucket(bucket)
                .destinationKey(destinationKey)
                .build();

        s3Client.copyObject(copyRequest);
        log.info("File copied from {} to {}", sourceKey, destinationKey);

        deleteModFile(sourceKey);

        return destinationKey;
    }

    public String getModFileUrl(String key, FileCategory fileCategory) {
        if (fileCategory == FileCategory.ARCHIVE) {
            // For archived files, return a pre-signed URL with temporary access
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } else {
            // For public files, return the direct URL
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.utilities().getUrl(getUrlRequest).toString();
        }
    }

    private String buildFolderPath(GameMod mod, FileCategory fileCategory) {
        return modFilesBaseFolder + mod.getId() + "/files/"
                + fileCategory.getDisplayName().replace(" ", "_").toLowerCase() + "/";
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed";
        }

        return fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_{2,}", "_")
                .replaceAll("^[._]+", "")
                .replaceAll("[._]+$", "");
    }
}