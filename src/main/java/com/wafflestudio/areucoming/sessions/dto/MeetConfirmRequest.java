package com.wafflestudio.areucoming.sessions.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MeetConfirmRequest {
    private Long userId;
    private BigDecimal lat;
    private BigDecimal lng;
}