package com.wafflestudio.areucoming.auth.controller;

import com.wafflestudio.areucoming.auth.dto.LoginRequest;
import com.wafflestudio.areucoming.auth.dto.LoginResponse;
import com.wafflestudio.areucoming.auth.dto.SignupRequest;
import com.wafflestudio.areucoming.auth.dto.SignupResponse;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest){
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .displayName(signupRequest.getNickname())
                .createdAt(LocalDateTime.now())
                .build();
        SignupResponse res = userService.signup(user);
        return new ResponseEntity<>(res,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse res = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
}
