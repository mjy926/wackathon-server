package com.wafflestudio.areucoming.sessions.controller;

import com.wafflestudio.areucoming.sessions.dto.*;
import com.wafflestudio.areucoming.sessions.model.Session;
import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import com.wafflestudio.areucoming.sessions.service.SessionService;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/sessions"})
public class SessionController {
    private final SessionService sessionService;
    private final UserService userService;

    /**
     * 세션 생성(요청)
     * - userId 기준으로 couple_id를 찾아 status = PENDING 세션 생성
     */
    @PostMapping("")
    public ResponseEntity<Session> create(@AuthenticationPrincipal String email) {
        Long userId = userService.getCurrentUserId(email);
        Session created = sessionService.createSessionRequest(userId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 내가 속한 커플의 세션 목록
     */
    @GetMapping("")
    public ResponseEntity<List<Session>> list(@AuthenticationPrincipal String email) {
        Long userId = userService.getCurrentUserId(email);
        return ResponseEntity.ok(sessionService.listSessionsForUser(userId));
    }

    /**
     * 현재 세션 상태 확인
     */
    @GetMapping("/{sessionId}/status")
    public ResponseEntity<SessionStatusResponse> status(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal String email
    ) {
        Long userId = userService.getCurrentUserId(email);
        Session s = sessionService.getSessionOrThrow(sessionId);
        // 접근 체크는 history에서 하는 방식과 동일하게 하기 위해 service 호출
        sessionService.getHistory(sessionId, userId, 0L, 1);
        return ResponseEntity.ok(new SessionStatusResponse(
                s.getId(), s.getCoupleId(), s.getRequestUserId(), s.getStatus(),
                s.getRequestedAt(), s.getStartAt(), s.getEndAt(), s.getEndReason(),
                s.getMeetAt(), s.getMeetLat(), s.getMeetLng()
        ));
    }

    /**
     * 세션 수락(PENDING -> ACTIVE)
     */
    @PostMapping("/{sessionId}/accept")
    public ResponseEntity<Session> accept(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal String email
    ) {
        Long userId = userService.getCurrentUserId(email);
        return ResponseEntity.ok(sessionService.acceptSession(sessionId, userId));
    }

    /**
     * 세션 종료 (ACTIVE/PENDING -> DONE)
     */
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<Session> finish(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal String email,
            @RequestBody FinishSessionRequest req
    ) {
        Long userId = userService.getCurrentUserId(email);
        return ResponseEntity.ok(sessionService.cancelOrFinish(sessionId, userId, req.getReason()));
    }

    /**
     * 사진 업로드 (multipart/form-data)
     * - file: 사진
     * - userId: 업로더
     */
    @PostMapping(value = "/{sessionId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SessionPoint> uploadPhoto(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal String email,
            @RequestPart("file") MultipartFile file,
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng
    ) {
        Long userId = userService.getCurrentUserId(email);
        return new ResponseEntity<>(
                sessionService.uploadPhoto(sessionId, userId, file, lat, lng),
                HttpStatus.CREATED
        );
    }

    /**
     * 현재 세션에서 발생한 기록(사진 포함) 모두 가져오기
     * - sinceId: 증분 로딩
     * - limit: 최대 개수
     */
    @GetMapping("/{sessionId}/history")
    public ResponseEntity<HistoryResponse> history(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal String email,
            @RequestParam(value = "sinceId", required = false) Long sinceId,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        Long userId = userService.getCurrentUserId(email);
        List<SessionPoint> points = sessionService.getHistory(sessionId, userId, sinceId, limit);
        return ResponseEntity.ok(new HistoryResponse(sessionId, points));
    }
}