package com.penclub.backend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.penclub.backend.auth.dto.RegisterRequest;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerCreatesNewMemberWhenEmailDoesNotExist() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe.integration@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane.doe.integration@example.com"))
                .andExpect(jsonPath("$.role").value("MEMBER"));

        assertThat(userRepository.findByEmailIgnoreCase("jane.doe.integration@example.com"))
                .hasValueSatisfying(user -> assertThat(user.getRole()).isEqualTo(Role.MEMBER));
    }
}
