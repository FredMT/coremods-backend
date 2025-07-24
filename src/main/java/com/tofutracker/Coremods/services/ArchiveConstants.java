package com.tofutracker.Coremods.services;

import java.util.Set;

public class ArchiveConstants {
    public static final Set<String> ARCHIVE_EXTENSIONS = Set.of(
            ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2", ".xz", ".tar.gz", ".tar.bz2", ".tar.xz");

    public static final long MAX_TOTAL_SIZE = 650000000L; // 650 MB
    public static final int MAX_FILE_COUNT = 100;
    public static final double MAX_COMPRESSION_RATIO = 10.0;
} 