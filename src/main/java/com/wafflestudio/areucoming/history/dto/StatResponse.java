package com.wafflestudio.areucoming.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatResponse {
    int totalMeetings;
    int averageMinutes;
    int averageDistance;
    int totalMinutes;
    int totalDistance;
    int minMinutes;
}
