package com.tofutracker.Coremods.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null);
    }
    
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(message, data);
    }
} 