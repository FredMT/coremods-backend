package com.tofutracker.Coremods.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.PasswordRequiredException;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
public class SevenZArchiveValidator extends BaseArchiveValidator {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".7z");
    }

    public Map<String, Object> validate(byte[] archiveBytes, String filename) throws IOException {
        Path tempFile = Files.createTempFile("temp_7z", ".7z");
        
        try {
            Files.write(tempFile, archiveBytes);

            try (SevenZFile sevenZFile = new SevenZFile.Builder().setFile(tempFile.toFile()).get()) {
                List<Map<String, Object>> filePreview = new ArrayList<>();
                List<String> nestedArchives = new ArrayList<>();

                SevenZArchiveEntry entry;
                int totalEntryCount = 0;
                long totalSizeArchive = 0;

                while ((entry = sevenZFile.getNextEntry()) != null) {
                    totalEntryCount++;

                    validateFileCount(totalEntryCount);

                    String entryName = entry.getName();
                    validatePathTraversal(entryName, "7Z");
                    validateNestedArchives(entryName);

                    Map<String, Object> fileInfo = createFileInfo(entryName, entry.isDirectory());

                    if (!entry.isDirectory()) {
                        byte[] buffer = new byte[8192];
                        int nBytes;

                        while ((nBytes = sevenZFile.read(buffer)) > 0) {
                            totalSizeArchive += nBytes;
                            
                            InputStreamStatistics stats = sevenZFile.getStatisticsForCurrentEntry();
                            long compressedCount = stats.getCompressedCount();
                            long uncompressedCount = stats.getUncompressedCount();

                            validateCompressionRatio(compressedCount, uncompressedCount, entryName);
                            validateTotalSize(totalSizeArchive);
                        }
                    }

                    filePreview.add(fileInfo);
                }

                return createSuccessResult(totalEntryCount, totalSizeArchive, nestedArchives, filePreview, "7Z");
                
            } catch (PasswordRequiredException e) {
                throw new IllegalArgumentException("7Z file is password protected or encrypted");
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IOException("Error processing 7Z file: " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                log.warn("Failed to delete temporary file: {}", tempFile);
            }
        }
    }
} 