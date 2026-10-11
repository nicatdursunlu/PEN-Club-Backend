package com.penclub.backend.user.controller;

import com.penclub.backend.user.dto.UserDto;
import com.penclub.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsersReturnsUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                UserDto.builder()
                        .id(UUID.randomUUID())
                        .email("admin@example.com")
                        .firstName("Admin")
                        .lastName("User")
                        .role("ADMIN")
                        .enabled(true)
                        .build(),
                UserDto.builder()
                        .id(UUID.randomUUID())
                        .email("member@example.com")
                        .firstName("Member")
                        .lastName("User")
                        .phoneNumber("+994501234567")
                        .role("MEMBER")
                        .enabled(true)
                        .build()
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].email").value("member@example.com"))
                .andExpect(jsonPath("$[1].phoneNumber").value("+994501234567"));
    }
}
