package com.wafflestudio.areucoming.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HistoryDto {
    Long id;
    LocalDateTime date;
    long travelMinutes;
    int distance;
}
