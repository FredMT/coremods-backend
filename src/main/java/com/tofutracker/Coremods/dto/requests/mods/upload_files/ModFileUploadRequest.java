package com.tofutracker.Coremods.dto.requests.mods.upload_files;

import com.tofutracker.Coremods.config.enums.FileCategory;
import com.tofutracker.Coremods.dto.annotation.ValidFileExtension;
import com.tofutracker.Coremods.dto.annotation.ValidFileMaxSize;
import com.tofutracker.Coremods.dto.annotation.ValidFileMimeType;
import com.tofutracker.Coremods.dto.annotation.ValidFileVersion;
import com.tofutracker.Coremods.dto.annotation.ValueOfEnum;
import com.tofutracker.Coremods.services.archive_validation.ArchiveConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidFileVersion
public class ModFileUploadRequest {

    @NotBlank(message = "File name is required")
    @Size(max = 50, message = "File name cannot exceed 50 characters")
    private String fileName;

    @NotBlank(message = "File version is required")
    @Size(max = 50, message = "File version cannot exceed 50 characters")
    private String fileVersion;

    @NotBlank(message = "File category is required")
    @ValueOfEnum(enumClass = FileCategory.class, message = "File type must be a valid file category")
    private FileCategory fileCategory;

    @Size(max = 255, message = "File description cannot exceed 255 characters")
    private String fileDescription;
    
    @NotNull(message = "isNewVersionOfExistingFile is required")
    private Boolean isNewVersionOfExistingFile;
    
    @Min(value = 1, message = "File ID must be greater than 0")
    private Long fileId;
    
    private Boolean removePreviousFileVersion;
    
    @NotNull(message = "Archive file is required")
    @ValidFileMaxSize(maxSize = ArchiveConstants.MAX_TOTAL_SIZE, message = "File size cannot exceed 650 MB")
    @ValidFileExtension(extensions = {"zip", "7z"}, message = "Only ZIP and 7Z archive files are allowed")
    @ValidFileMimeType(mimeTypes = {"application/zip", "application/x-zip-compressed", "application/x-7z-compressed"}, 
            message = "Invalid file format. Only ZIP and 7Z archive files are allowed")
    private MultipartFile archiveFile;
}
