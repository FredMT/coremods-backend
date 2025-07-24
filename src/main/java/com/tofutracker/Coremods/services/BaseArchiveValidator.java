package com.tofutracker.Coremods.services;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tofutracker.Coremods.services.ArchiveConstants.*;

@Slf4j
public abstract class BaseArchiveValidator implements IArchiveValidator {

    protected void validateFileCount(int currentCount) {
        if (currentCount > MAX_FILE_COUNT) {
            throw new IllegalArgumentException(
                    "Archive bomb detected: Too many files (>" + MAX_FILE_COUNT + ")");
        }
    }

    protected void validateNestedArchives(String entryName) {
        String fileName = entryName.toLowerCase();
        for (String ext : ARCHIVE_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                throw new IllegalArgumentException(
                        "Archive contains nested archive: " + entryName);
            }
        }
    }

    protected void validatePathTraversal(String entryName, String archiveType) throws IOException {
        File tempExtractDir = File.createTempFile(archiveType.toLowerCase() + "_extract_test", "").getParentFile();
        File extractedFile = new File(tempExtractDir, entryName);
        String canonicalPath = extractedFile.getCanonicalPath();
        String canonicalExtractDir = tempExtractDir.getCanonicalPath();

        if (!canonicalPath.startsWith(canonicalExtractDir + File.separator)) {
            throw new IllegalArgumentException(
                    archiveType + " slip detected: Entry attempts to write outside target directory: " + entryName);
        }
    }

    protected void validateCompressionRatio(long compressedCount, long uncompressedCount, String entryName) {
        if (compressedCount > 0) {
            double compressionRatio = (double) uncompressedCount / compressedCount;
            if (compressionRatio > MAX_COMPRESSION_RATIO) {
                throw new IllegalArgumentException(
                        "Archive bomb detected: Suspicious compression ratio for " +
                                entryName + " (ratio: " + String.format("%.2f", compressionRatio) + ")");
            }
        }
    }

    protected void validateTotalSize(long totalSize) {
        if (totalSize > MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException(
                    "Archive bomb detected: Total uncompressed size too large: " + totalSize + " bytes");
        }
    }

    protected Map<String, Object> createFileInfo(String entryName, boolean isDirectory) {
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("name", entryName);
        fileInfo.put("isDirectory", isDirectory);

        if (!isDirectory) {
            fileInfo.put("extension", getFileExtension(entryName));
        }

        return fileInfo;
    }

    protected String getFileExtension(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex);
        }
        return extension;
    }

    protected Map<String, Object> createSuccessResult(
            int totalEntryCount, 
            long totalUncompressedSize, 
            List<String> nestedArchives, 
            List<Map<String, Object>> filePreview, 
            String archiveType) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("fileCount", totalEntryCount);
        result.put("filePreview", filePreview);
        return result;
    }
} 