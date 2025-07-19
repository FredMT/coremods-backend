package com.tofutracker.Coremods.services.bug_report;

import com.tofutracker.Coremods.config.enums.BugReportPriority;
import com.tofutracker.Coremods.config.enums.BugReportStatus;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.CreateBugReportRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.CreateCommentBugReportRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.UpdateBugReportPriorityRequest;
import com.tofutracker.Coremods.dto.requests.mods.bug_reports.UpdateBugReportStatusRequest;
import com.tofutracker.Coremods.dto.responses.ApiResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportPriorityUpdateResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportResponse;
import com.tofutracker.Coremods.dto.responses.mods.bug_reports.BugReportStatusUpdateResponse;
import com.tofutracker.Coremods.entity.BugReport;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.entity.Comment;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.BugReportRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import com.tofutracker.Coremods.repository.CommentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BugReportService {

    private final BugReportRepository bugReportRepository;
    private final GameModRepository gameModRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public BugReportResponse createBugReport(Long modId, CreateBugReportRequest request, User user) {
        validateUserForOperation(user);

        GameMod mod = gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        BugReport bugReport = BugReport.builder()
                .user(user)
                .mod(mod)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        BugReport savedBugReport = bugReportRepository.save(bugReport);
        return BugReportResponse.fromEntity(savedBugReport);
    }

    public List<BugReportResponse> getBugReportsByModId(Long modId) {
        // Verify the mod exists
        gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        return bugReportRepository.findByModIdOrderByCreatedAtDesc(modId).stream()
                .map(BugReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BugReportStatusUpdateResponse updateBugReportStatus(Long bugReportId, UpdateBugReportStatusRequest request,
            User user) {
        validateUserForOperation(user);

        BugReport bugReport = bugReportRepository.findById(bugReportId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug report not found with id: " + bugReportId));

        GameMod mod = bugReport.getMod();
        if (!Objects.equals(mod.getAuthor().getId(), user.getId())) {
            throw new ForbiddenException("Only the mod author can update bug report status");
        }

        BugReportStatus newStatus = BugReportStatus.valueOf(request.getStatus());

        if (newStatus == bugReport.getStatus()) {
            throw new BadRequestException("Bug report status is already " + newStatus);
        }

        bugReport.setStatus(newStatus);

        BugReport updatedBugReport = bugReportRepository.save(bugReport);

        return BugReportStatusUpdateResponse.builder()
                .bugReportId(updatedBugReport.getId())
                .status(updatedBugReport.getStatus())
                .build();
    }

    public BugReportPriorityUpdateResponse updateBugReportPriority(Long bugReportId,
            UpdateBugReportPriorityRequest request, User user) {
        validateUserForOperation(user);

        BugReport bugReport = bugReportRepository.findById(bugReportId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug report not found with id: " + bugReportId));

        GameMod mod = bugReport.getMod();
        if (!Objects.equals(mod.getAuthor().getId(), user.getId())) {
            throw new ForbiddenException("Only the mod author can update bug report priority");
        }

        BugReportPriority newPriority = BugReportPriority.valueOf(request.getPriority());

        if (newPriority == bugReport.getPriority()) {
            throw new BadRequestException("Bug report priority is already " + newPriority);
        }

        bugReport.setPriority(newPriority);

        BugReport updatedBugReport = bugReportRepository.save(bugReport);

        return BugReportPriorityUpdateResponse.builder()
                .bugReportId(updatedBugReport.getId())
                .priority(updatedBugReport.getPriority())
                .build();
    }

    public void deleteBugReport(Long bugReportId, User user) {

        BugReport bugReport = bugReportRepository.findById(bugReportId)
                .orElseThrow(() -> new ResourceNotFoundException("Bug report not found with id: " + bugReportId));

        GameMod mod = bugReport.getMod();
        if (!Objects.equals(mod.getAuthor().getId(), user.getId())) {
            throw new ForbiddenException("Only the mod author can delete bug report");
        }

        bugReportRepository.delete(bugReport);
    }

    public ResponseEntity<ApiResponse<Void>> createCommentOnBugReport(BugReport bugReport, User currentUser,
            @Valid CreateCommentBugReportRequest request) {

        if (commentRepository.findByCommentableTypeAndCommentableIdAndParentId(
                "bug_report", bugReport.getId(), null).isPresent()) {
            throw new BadRequestException("There already exists a main comment on this bug report.");
        }

        Comment comment = Comment.builder()
                .commentableType("bug_report")
                .commentableId(bugReport.getId())
                .user(currentUser)
                .content(request.getContent())
                .parentType("bug_report")
                .build();

        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Commented on bug report successfully."));
    }

    public ResponseEntity<ApiResponse<Void>> replyToBugReportComment(BugReport bugReport, Comment comment,
            User currentUser, @Valid CreateCommentBugReportRequest request) {

        Comment reply = Comment.builder()
                .commentableType("bug_report")
                .commentableId(bugReport.getId())
                .user(currentUser)
                .content(request.getContent())
                .parentId(comment.getId())
                .parentType("comment")
                .build();

        commentRepository.save(reply);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Replied to bug report comment successfully."));
    }

    private void validateUserForOperation(User user) {
        if (user == null) {
            throw new UnauthorizedException("User must be authenticated");
        }

        // TODO: ensure user can create bug report ONLY for mod they have downloaded

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("User must have verified email.");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is disabled.");
        }

        if (!user.isAccountNonExpired()) {
            throw new UnauthorizedException("User account is expired.");
        }

        if (!user.isAccountNonLocked()) {
            throw new UnauthorizedException("User account is locked.");
        }

        if (!user.isCredentialsNonExpired()) {
            throw new UnauthorizedException("User credentials are expired.");
        }
    }

}