package com.wafflestudio.areucoming.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SessionHistoryResponse {
    List<PointHistoryDto> user1;
    List<PointHistoryDto> user2;
}
