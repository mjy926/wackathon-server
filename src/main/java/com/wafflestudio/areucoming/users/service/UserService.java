package com.wafflestudio.areucoming.users.service;

import com.wafflestudio.areucoming.auth.dto.LoginResponse;
import com.wafflestudio.areucoming.auth.dto.SignupResponse;
import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.auth.service.AuthService;
import com.wafflestudio.areucoming.users.exceptions.InvalidPasswordException;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserDto getCurrentUser(String email){
        User user = userRepository.findByEmail(email);
        return new UserDto(user.getId(), user.getEmail(), user.getDisplayName(), user.getProfileImageUrl());
    }

    public Long getCurrentUserId(String email){
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    public SignupResponse signup(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User toSave = User.builder()
                .email(user.getEmail())
                .password(encodedPassword)
                .displayName(user.getDisplayName())
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(toSave);


        UserDto userDto = new UserDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getDisplayName(),
                savedUser.getProfileImageUrl()
        );
        String accessToken = authService.createAccessToken(savedUser.getEmail());
        String refreshToken = authService.createRefreshToken(savedUser.getEmail());
        return new SignupResponse(accessToken, refreshToken, userDto);
    }

    public LoginResponse login(String email, String password){
        User user = userRepository.findByEmail(email);
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidPasswordException("Invalid Password");
        }
        UserDto userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getProfileImageUrl()
        );
        String accessToken = authService.createAccessToken(user.getEmail());
        String refreshToken = authService.createRefreshToken(user.getEmail());
        return new LoginResponse(accessToken, refreshToken, userDto);
    }

    public void updateProfileImageUrl(String email, String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            throw new IllegalArgumentException("profileImageUrl is required");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }

        User updatedUser = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .password(user.getPassword())
                .token(user.getToken())
                .profileImageUrl(profileImageUrl)
                .build();

        userRepository.save(updatedUser);
    }
}
