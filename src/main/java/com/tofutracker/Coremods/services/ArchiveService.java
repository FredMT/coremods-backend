package com.tofutracker.Coremods.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final List<IArchiveValidator> archiveValidators;

    public Map<String, Object> validateArchive(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        IArchiveValidator validator = archiveValidators.stream()
                .filter(v -> v.supports(filename))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported archive format. Supported formats: ZIP, 7Z"));

        byte[] archiveBytes = file.getBytes();
        return validator.validate(archiveBytes, filename);
    }
}