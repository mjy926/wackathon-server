package com.wafflestudio.areucoming.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
}
