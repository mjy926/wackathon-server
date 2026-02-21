package com.wafflestudio.areucoming.sessions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActiveSessionResponse {
    private Long sessionId; // null if none
}