package com.tofutracker.Coremods.services.bug_report;

import com.tofutracker.Coremods.config.enums.BugReportPriority;
import com.tofutracker.Coremods.config.enums.BugReportStatus;
import com.tofutracker.Coremods.dto.requests.bug_report.CreateBugReportRequest;
import com.tofutracker.Coremods.dto.requests.bug_report.UpdateBugReportPriorityRequest;
import com.tofutracker.Coremods.dto.requests.bug_report.UpdateBugReportStatusRequest;
import com.tofutracker.Coremods.dto.responses.BugReportPriorityUpdateResponse;
import com.tofutracker.Coremods.dto.responses.BugReportResponse;
import com.tofutracker.Coremods.dto.responses.BugReportStatusUpdateResponse;
import com.tofutracker.Coremods.entity.BugReport;
import com.tofutracker.Coremods.entity.GameMod;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.exception.BadRequestException;
import com.tofutracker.Coremods.exception.ForbiddenException;
import com.tofutracker.Coremods.exception.ResourceNotFoundException;
import com.tofutracker.Coremods.exception.UnauthorizedException;
import com.tofutracker.Coremods.repository.BugReportRepository;
import com.tofutracker.Coremods.repository.GameModRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @Transactional(readOnly = true)
    public List<BugReportResponse> getBugReportsByModId(Long modId) {
        // Verify the mod exists
        gameModRepository.findById(modId)
                .orElseThrow(() -> new ResourceNotFoundException("Game mod not found with id: " + modId));

        return bugReportRepository.findByModIdOrderByCreatedAtDesc(modId).stream()
                .map(BugReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
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

    @Transactional
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