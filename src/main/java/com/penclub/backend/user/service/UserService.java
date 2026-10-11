package com.penclub.backend.user.service;

import com.penclub.backend.exception.UserAlreadyExistsException;
import com.penclub.backend.user.dto.UpdateProfileRequest;
import com.penclub.backend.user.dto.UserDto;
import com.penclub.backend.user.entity.User;
import com.penclub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto updateProfile(User currentUser, UpdateProfileRequest request) {
        if (request.getFirstName() != null) {
            currentUser.setFirstName(normalizeRequired(request.getFirstName(), "First name"));
        }
        if (request.getLastName() != null) {
            currentUser.setLastName(normalizeRequired(request.getLastName(), "Last name"));
        }
        if (request.getEmail() != null) {
            String normalizedEmail = normalizeEmail(request.getEmail());
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, currentUser.getId())) {
                throw new UserAlreadyExistsException(
                        "User with email '" + normalizedEmail + "' already exists"
                );
            }
            currentUser.setEmail(normalizedEmail);
        }
        if (request.getGender() != null) {
            currentUser.setGender(normalize(request.getGender()));
        }
        if (request.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(normalize(request.getPhoneNumber()));
        }
        if (request.getEducationDegree() != null) {
            currentUser.setEducationDegree(normalize(request.getEducationDegree()));
        }

        User savedUser = userRepository.save(currentUser);
        return UserDto.fromEntity(savedUser);
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }

    private String normalizeEmail(String email) {
        String normalized = normalizeRequired(email, "Email");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
