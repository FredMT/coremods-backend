package com.tofutracker.Coremods.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
public class ZipArchiveValidator extends BaseArchiveValidator {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".zip");
    }

    public Map<String, Object> validate(byte[] archiveBytes, String filename) throws IOException {
        Path tempFile = Files.createTempFile("temp_zip", ".zip");
        
        try {
            Files.write(tempFile, archiveBytes);

            try (ZipFile zipFile = new ZipFile.Builder().setFile(tempFile.toFile()).get()) {
                List<Map<String, Object>> filePreview = new ArrayList<>();
                List<String> nestedArchives = new ArrayList<>();

                Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
                int totalEntryCount = 0;
                long totalSizeArchive = 0;

                while (entries.hasMoreElements()) {
                    ZipArchiveEntry entry = entries.nextElement();
                    totalEntryCount++;

                    validateFileCount(totalEntryCount);

                    String entryName = entry.getName();
                    validatePathTraversal(entryName, "ZIP");
                    validateNestedArchives(entryName);

                    if (entry.getGeneralPurposeBit().usesEncryption()) {
                        throw new IllegalArgumentException("ZIP file is password protected or encrypted");
                    }

                    Map<String, Object> fileInfo = createFileInfo(entryName, entry.isDirectory());

                    if (!entry.isDirectory()) {
                        try (InputStream entryInputStream = zipFile.getInputStream(entry)) {
                            InputStreamStatistics stats = (InputStreamStatistics) entryInputStream;
                            
                            byte[] buffer = new byte[8192];
                            int nBytes;

                            while ((nBytes = entryInputStream.read(buffer)) > 0) {
                                long compressedCount = stats.getCompressedCount();
                                long uncompressedCount = stats.getUncompressedCount();
                                totalSizeArchive += nBytes;

                                validateCompressionRatio(compressedCount, uncompressedCount, entryName);
                                validateTotalSize(totalSizeArchive);
                            }
                        }
                    }

                    filePreview.add(fileInfo);
                }

                return createSuccessResult(totalEntryCount, totalSizeArchive, nestedArchives, filePreview, "ZIP");
            }
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                log.warn("Failed to delete temporary file: {}", tempFile);
            }
        }
    }
} 