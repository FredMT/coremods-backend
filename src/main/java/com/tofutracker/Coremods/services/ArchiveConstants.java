package com.tofutracker.Coremods.services;

import java.util.Set;

public class ArchiveConstants {
    public static final Set<String> ARCHIVE_EXTENSIONS = Set.of(
            ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2", ".xz", ".tar.gz", ".tar.bz2", ".tar.xz");

    public static final long MAX_TOTAL_SIZE = 100000000L; // 100 MB
    public static final int MAX_FILE_COUNT = 250;
    public static final double MAX_COMPRESSION_RATIO = 10.0;
} 