package com.tofutracker.Coremods.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipTestService {

    private final ArchiveService archiveService;

    public Map<String, Object> validateZip(MultipartFile file) throws IOException {
        return archiveService.validateArchive(file);
    }
}
