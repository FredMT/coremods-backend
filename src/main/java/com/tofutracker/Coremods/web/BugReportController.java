package com.tofutracker.Coremods.web;

import com.tofutracker.Coremods.dto.requests.mods.bug_reports.CreateBugReportRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.CreateCommentBugReportRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.UpdateBugReportPriorityRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.UpdateBugReportStatusRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportPriorityUpdateResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportStatusUpdateResponse;
import com.tofutracker.Coremods.entity.Comment;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.bug_report.BugReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                return ResponseEntity.ok(ApiResponse.success("Bug report created successfully", bugReport));
        }

        @PutMapping("/{bugReportId}/status")
        public ResponseEntity<ApiResponse<BugReportStatusUpdateResponse>> updateBugReportStatus(
                        @PathVariable Long bugReportId,
                        @Valid @RequestBody UpdateBugReportStatusRequest request,
                        @AuthenticationPrincipal User currentUser) {

                BugReportStatusUpdateResponse response = bugReportService.updateBugReportStatus(bugReportId, request,
                                currentUser);
                return ResponseEntity.ok(ApiResponse.success("Bug report status updated successfully", response));
        }

        @PutMapping("/{bugReportId}/priority")
        public ResponseEntity<ApiResponse<BugReportPriorityUpdateResponse>> updateBugReportPriority(
                        @PathVariable Long bugReportId,
                        @Valid @RequestBody UpdateBugReportPriorityRequest request,
                        @AuthenticationPrincipal User currentUser) {

                BugReportPriorityUpdateResponse response = bugReportService.updateBugReportPriority(bugReportId,
                                request,
                                currentUser);
                return ResponseEntity.ok(ApiResponse.success("Bug report priority updated successfully", response));
        }

        @DeleteMapping("/{bugReportId}")
        public ResponseEntity<ApiResponse<Void>> deleteBugReport(@PathVariable Long bugReportId,
                        @AuthenticationPrincipal User currentUser) {
                bugReportService.deleteBugReport(bugReportId, currentUser);
                return ResponseEntity.ok(ApiResponse.success("Bug report deleted successfully"));
        }

        @PostMapping("/{bugReportId}/comment")
        public ResponseEntity<ApiResponse<Comment>> commentBugReport(
                        @PathVariable("bugReportId") Long bugReportId,
                        @AuthenticationPrincipal User currentUser,
                        @Valid @RequestBody CreateCommentBugReportRequest request) {
                Comment comment = bugReportService.createCommentOnBugReport(bugReportId, currentUser, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Commented on bug report successfully.", comment));
        }

        @PostMapping("/{bugReportId}/comment/{commentId}")
        public ResponseEntity<ApiResponse<Comment>> replyToComment(
                        @PathVariable("bugReportId") Long bugReportId,
                        @PathVariable("commentId") Long commentId,
                        @AuthenticationPrincipal User currentUser,
                        @Valid @RequestBody CreateCommentBugReportRequest request) {
                Comment reply = bugReportService.replyToBugReportComment(bugReportId, commentId, currentUser, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Commented on bug report successfully.", reply));
        }
}