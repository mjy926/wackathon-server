package com.wafflestudio.areucoming.users.controller;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.users.model.User;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal String email) {
        UserDto dto = userService.getCurrentUser(email);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
}
