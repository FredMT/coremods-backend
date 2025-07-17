package com.tofutracker.Coremods.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tofutracker.Coremods.dto.requests.bug_report.CreateBugReportRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.BugReportResponse;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.bug_report.BugReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/bug-reports")
@RequiredArgsConstructor
public class BugReportController {

    private final BugReportService bugReportService;

    @GetMapping("/{modId}")
    public ResponseEntity<ApiResponse<List<BugReportResponse>>> getBugReportsByMod(@PathVariable Long modId) {
        List<BugReportResponse> bugReports = bugReportService.getBugReportsByModId(modId);
        return ResponseEntity.ok(ApiResponse.success("Bug reports retrieved successfully", bugReports));
    }

    @PostMapping("/{modId}")
    public ResponseEntity<ApiResponse<BugReportResponse>> createBugReport(
            @PathVariable Long modId,
            @Valid @RequestBody CreateBugReportRequest request,
            @AuthenticationPrincipal User currentUser) {

        BugReportResponse bugReport = bugReportService.createBugReport(modId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bug report created successfully", bugReport));
    }
}