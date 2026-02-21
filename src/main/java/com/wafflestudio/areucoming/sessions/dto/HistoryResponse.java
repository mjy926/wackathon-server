package com.wafflestudio.areucoming.sessions.dto;

import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HistoryResponse {
    private Long sessionId;
    private List<SessionPoint> points;
}