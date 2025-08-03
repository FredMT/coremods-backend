package com.tofutracker.Coremods.services.images;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tofutracker.Coremods.entity.Image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final S3Client s3Client;
    private final S3TransferManager transferManager;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

        public void uploadImage(MultipartFile file, String storageKey) throws IOException {
            try {
                PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(storageKey)
                        .acl(ObjectCannedACL.PUBLIC_READ);

                String contentType = file.getContentType();
                if (contentType != null && !contentType.isEmpty()) {
                    requestBuilder.contentType(contentType);
                }

                UploadRequest uploadRequest = UploadRequest.builder()
                        .putObjectRequest(requestBuilder.build())
                        .addTransferListener(LoggingTransferListener.create()) 
                        .requestBody(AsyncRequestBody.fromBytes(file.getBytes()))
                        .build();

                Upload upload = transferManager.upload(uploadRequest);

                upload.completionFuture().join();
                log.info("Image uploaded successfully to storage: {}", storageKey);
            } catch (Exception e) {
                log.error("Failed to upload image: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload image to storage", e);
            }
        }

    public void deleteImage(String storageKey) {
        software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteRequest = software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(storageKey)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("Image deleted from storage: {}", storageKey);
    }

    public String getImageUrl(Image image) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucket)
                .key(image.getStorageKey())
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }
}