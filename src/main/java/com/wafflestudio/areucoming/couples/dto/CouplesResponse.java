package com.wafflestudio.areucoming.couples.dto;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouplesResponse {
    Long id;
    UserDto user1;
    UserDto user2;
}
