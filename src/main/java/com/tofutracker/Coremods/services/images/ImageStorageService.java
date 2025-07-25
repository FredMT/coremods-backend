package com.tofutracker.Coremods.services.images;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.tofutracker.Coremods.entity.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final AmazonS3 s3Client;

    @Value("${do.space.bucket}")
    private String doSpaceBucket;

    @Value("${do.space.folder:images/}")
    private String imageFolder;

    public String uploadImage(MultipartFile file, String uniqueImageName) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String key = imageFolder + uniqueImageName + "." + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        String contentType = file.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            metadata.setContentType(contentType);
        }

        s3Client.putObject(new PutObjectRequest(doSpaceBucket, key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        log.info("Image uploaded successfully to S3: {}", key);
        return key;
    }

    public void deleteImage(Image image) {
        String key = imageFolder + image.getName() + "." + image.getExt();
        s3Client.deleteObject(new DeleteObjectRequest(doSpaceBucket, key));
        log.info("Image deleted from S3: {}", key);
    }

    public String getImageUrl(Image image) {
        String key = imageFolder + image.getName() + "." + image.getExt();
        return s3Client.getUrl(doSpaceBucket, key).toString();
    }

    public String generateUniqueImageName(String originalFilename) {
        String imageName = FilenameUtils.removeExtension(originalFilename);
        return imageName + "_" + UUID.randomUUID().toString().replace("-", "");
    }
} 