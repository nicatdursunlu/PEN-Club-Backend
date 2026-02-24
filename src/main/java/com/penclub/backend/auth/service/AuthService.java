package com.penclub.backend.auth.service;

import com.penclub.backend.auth.dto.AuthResponse;
import com.penclub.backend.auth.dto.LoginRequest;
import com.penclub.backend.auth.dto.RefreshTokenRequest;
import com.penclub.backend.auth.dto.RegisterRequest;
import com.penclub.backend.exception.UserAlreadyExistsException;
import com.penclub.backend.security.JwtService;
import com.penclub.backend.token.entity.RefreshToken;
import com.penclub.backend.token.service.RefreshTokenService;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.entity.User;
import com.penclub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    /**
     * Registers a new user with the MEMBER role by default.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        return registerWithRole(request, Role.MEMBER);
    }

    /**
     * Registers a new user with the ADMIN role.
     * Only callable by an existing ADMIN (enforced at controller level via @PreAuthorize).
     */
    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        return registerWithRole(request, Role.ADMIN);
    }

    /**
     * Shared registration logic for any role.
     */
    private AuthResponse registerWithRole(RegisterRequest request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with email '" + request.getEmail() + "' already exists"
            );
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New {} registered: {}", role.name(), savedUser.getEmail());

        String accessToken = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return buildAuthResponse(savedUser, accessToken, refreshToken.getToken());
    }

    /**
     * Authenticates a user and returns JWT tokens.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();
        log.info("User logged in: {}", user.getEmail());

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    /**
     * Refreshes the access token using a valid refresh token.
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user);
        log.debug("Access token refreshed for user: {}", user.getEmail());

        return buildAuthResponse(user, newAccessToken, refreshToken.getToken());
    }

    /**
     * Logs out the user by revoking all their refresh tokens.
     */
    @Transactional
    public void logout(User currentUser) {
        refreshTokenService.revokeAllUserTokens(currentUser);
        log.info("User logged out: {}", currentUser.getEmail());
    }

    // --- Helper ---

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
