package com.penclub.backend.user.service;

import com.penclub.backend.exception.UserAlreadyExistsException;
import com.penclub.backend.user.dto.UpdateProfileRequest;
import com.penclub.backend.user.dto.UserDto;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.entity.User;
import com.penclub.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateProfileUpdatesCurrentUserProfileFields() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Nijat")
                .lastName("Dursunlu")
                .email("nijat.dursunlu@gmail.com")
                .password("encoded-password")
                .role(Role.MEMBER)
                .enabled(true)
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName(" Nijat ")
                .lastName(" Dursunlu ")
                .email(" Nijat.Updated@Example.com ")
                .gender(" male ")
                .phoneNumber(" +994501234567 ")
                .educationDegree(" Bachelor ")
                .build();

        when(userRepository.existsByEmailIgnoreCaseAndIdNot("nijat.updated@example.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto response = userService.updateProfile(user, request);

        assertThat(response.getFirstName()).isEqualTo("Nijat");
        assertThat(response.getLastName()).isEqualTo("Dursunlu");
        assertThat(response.getEmail()).isEqualTo("nijat.updated@example.com");
        assertThat(response.getGender()).isEqualTo("male");
        assertThat(response.getPhoneNumber()).isEqualTo("+994501234567");
        assertThat(response.getEducationDegree()).isEqualTo("Bachelor");
    }

    @Test
    void updateProfileStoresBlankValuesAsNullAndLeavesOmittedFieldsUnchanged() {
        User user = User.builder()
                .firstName("Nijat")
                .lastName("Dursunlu")
                .email("nijat.dursunlu@gmail.com")
                .password("encoded-password")
                .gender("male")
                .phoneNumber("+994501234567")
                .educationDegree("Bachelor")
                .role(Role.MEMBER)
                .enabled(true)
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .gender(" ")
                .phoneNumber("")
                .educationDegree(null)
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto response = userService.updateProfile(user, request);

        assertThat(response.getGender()).isNull();
        assertThat(response.getPhoneNumber()).isNull();
        assertThat(response.getEducationDegree()).isEqualTo("Bachelor");
    }

    @Test
    void updateProfileThrowsWhenEmailBelongsToAnotherUser() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Nijat")
                .lastName("Dursunlu")
                .email("nijat.dursunlu@gmail.com")
                .password("encoded-password")
                .role(Role.MEMBER)
                .enabled(true)
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.existsByEmailIgnoreCaseAndIdNot("existing@example.com", userId)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(user, request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email 'existing@example.com' already exists");
    }
}
