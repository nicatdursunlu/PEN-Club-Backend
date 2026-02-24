package com.penclub.backend.auth.controller;

import com.penclub.backend.auth.dto.AuthResponse;
import com.penclub.backend.auth.dto.LoginRequest;
import com.penclub.backend.auth.dto.RefreshTokenRequest;
import com.penclub.backend.auth.dto.RegisterRequest;
import com.penclub.backend.auth.service.AuthService;
import com.penclub.backend.user.dto.UserDto;
import com.penclub.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user registration, login, token refresh and logout")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with MEMBER role and returns JWT access & refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error (missing/invalid fields)", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/admin/register
     */
    @Operation(
            summary = "Register a new admin user",
            description = "Creates a new user account with ADMIN role. Requires an existing ADMIN Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error (missing/invalid fields)", content = @Content)
    })
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        log.info("Admin registration request received for email: {}", request.getEmail());
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login
     */
    @Operation(
            summary = "Login",
            description = "Authenticates a user with email and password. Returns JWT access token (15 min) and refresh token (7 days)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/refresh-token
     */
    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new JWT access token. The old refresh token is revoked (rotation)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Refresh token expired or revoked", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     */
    @Operation(
            summary = "Logout",
            description = "Revokes all refresh tokens for the currently authenticated user. Requires a valid Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal User currentUser) {
        authService.logout(currentUser);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * GET /api/v1/auth/me
     */
    @Operation(
            summary = "Get current user",
            description = "Returns the profile of the currently authenticated user. Requires a valid Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile returned successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(UserDto.fromEntity(currentUser));
    }
}
