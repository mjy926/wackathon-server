package com.wafflestudio.areucoming.history.dto;

import com.wafflestudio.areucoming.sessions.model.SessionPointType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointHistoryDto {
    SessionPointType type;
    LocalDateTime createdAt;
    BigDecimal lat;
    BigDecimal lng;
    String photoPath;
    String text;
}
