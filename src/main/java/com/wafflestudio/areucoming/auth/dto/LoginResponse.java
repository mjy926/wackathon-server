package com.wafflestudio.areucoming.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private UserDto user;
}
