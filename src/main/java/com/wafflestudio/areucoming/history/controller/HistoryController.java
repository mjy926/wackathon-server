package com.wafflestudio.areucoming.history.controller;

import com.wafflestudio.areucoming.history.dto.*;
import com.wafflestudio.areucoming.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/history")
@RestController
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{session_id}")
    public ResponseEntity<SessionHistoryResponse> getHistory(@PathVariable("session_id") String sessionId,
                                                            @AuthenticationPrincipal String email) {
        SessionHistoryResponse res = historyService.getSessionHistory(Long.valueOf(sessionId), email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<SessionHistoryResponse> getSessionPointList(@AuthenticationPrincipal String email){
        SessionHistoryResponse res = historyService.getSessionPoints(email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/stat")
    public ResponseEntity<StatResponse> getStat(@AuthenticationPrincipal String email){
        StatResponse res = historyService.getStat(email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<HistoryListResponse> getHistoryList(@AuthenticationPrincipal String email){
        List<HistoryDto> historyList = historyService.getHistoryList(email);
        HistoryListResponse res = new HistoryListResponse(historyList);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
