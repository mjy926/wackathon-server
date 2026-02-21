package com.wafflestudio.areucoming.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDto {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
}
