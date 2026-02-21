package com.wafflestudio.areucoming.history.controller;

import com.wafflestudio.areucoming.history.dto.SessionHistoryResponse;
import com.wafflestudio.areucoming.history.dto.SessionIdResponse;
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
        SessionHistoryResponse res = historyService.getHistory(Long.valueOf(sessionId), email);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<SessionIdResponse> getHistoryList(@AuthenticationPrincipal String email){
        List<Long> idList = historyService.getHistoryIdList(email);
        SessionIdResponse res = new SessionIdResponse(idList);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
