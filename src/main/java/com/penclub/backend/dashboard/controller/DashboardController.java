package com.penclub.backend.dashboard.controller;

import com.penclub.backend.dashboard.dto.DashboardResponseDto;
import com.penclub.backend.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Admin dashboard statistics endpoint")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get dashboard stats",
            description = "Returns aggregated statistics for the admin dashboard. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard stats returned successfully",
                    content = @Content(schema = @Schema(implementation = DashboardResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required", content = @Content)
    })
    public ResponseEntity<DashboardResponseDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
