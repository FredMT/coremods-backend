package com.tofutracker.Coremods.services.images;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tofutracker.Coremods.entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final AmazonS3 s3Client;

    @Value("${do.space.bucket}")
    private String doSpaceBucket;

    public void uploadImage(MultipartFile file, String storageKey) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            metadata.setContentType(contentType);
        }

        s3Client.putObject(new PutObjectRequest(doSpaceBucket, storageKey, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        log.info("Image uploaded successfully to storage: {}", storageKey);
    }

    public void deleteImage(Image image) {
        s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, image.getStorageKey()));
        log.info("Image deleted from storage: {}", image.getStorageKey());
    }

    public String getImageUrl(Image image) {
        return s3Client.getUrl(doSpaceBucket, image.getStorageKey()).toString();
    }
} 