package com.wafflestudio.areucoming.sessions.dto;

import com.wafflestudio.areucoming.sessions.model.EndReason;
import lombok.Getter;

@Getter
public class FinishSessionRequest {
    private EndReason reason; // optional: default MANUAL_CANCEL
}