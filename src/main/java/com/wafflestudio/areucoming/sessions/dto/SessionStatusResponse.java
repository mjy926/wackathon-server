package com.wafflestudio.areucoming.sessions.dto;

import com.wafflestudio.areucoming.sessions.model.EndReason;
import com.wafflestudio.areucoming.sessions.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionStatusResponse {
    private Long sessionId;
    private Long coupleId;
    private Long requestUserId;
    private SessionStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private EndReason endReason;
    private LocalDateTime meetAt;
    private BigDecimal meetLat;
    private BigDecimal meetLng;
}