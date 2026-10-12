package com.penclub.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.penclub.backend.auth.dto.AuthResponse;
import com.penclub.backend.auth.dto.LoginRequest;
import com.penclub.backend.auth.dto.RefreshTokenRequest;
import com.penclub.backend.auth.dto.RegisterRequest;
import com.penclub.backend.auth.service.AuthService;
import com.penclub.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registerEndpointReturnsCreatedMemberAuthResponse() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void loginEndpointReturnsAuthResponse() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    void refreshTokenEndpointAcceptsCamelCaseRefreshToken() throws Exception {
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse());

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        org.mockito.ArgumentCaptor<RefreshTokenRequest> requestCaptor =
                org.mockito.ArgumentCaptor.forClass(RefreshTokenRequest.class);
        verify(authService).refreshToken(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void adminRegisterEndpointIsNotExposed() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin.user@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    private AuthResponse authResponse() {
        return AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .userId(UUID.randomUUID())
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("MEMBER")
                .build();
    }
}
