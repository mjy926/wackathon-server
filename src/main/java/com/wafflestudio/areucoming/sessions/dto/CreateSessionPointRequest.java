package com.wafflestudio.areucoming.sessions.dto;

import com.wafflestudio.areucoming.sessions.model.SessionPointType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateSessionPointRequest {
    private SessionPointType type;
    private BigDecimal lat;
    private BigDecimal lng;
    private String text; // type == MEMO 일때
}