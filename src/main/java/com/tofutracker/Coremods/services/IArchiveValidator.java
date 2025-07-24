package com.tofutracker.Coremods.services;

import java.io.IOException;
import java.util.Map;

public interface IArchiveValidator {
    boolean supports(String filename);
    Map<String, Object> validate(byte[] archiveBytes, String filename) throws IOException;
} 