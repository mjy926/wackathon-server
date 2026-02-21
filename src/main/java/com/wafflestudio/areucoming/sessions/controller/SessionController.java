package com.wafflestudio.areucoming.sessions.controller;

import com.wafflestudio.areucoming.sessions.dto.*;
import com.wafflestudio.areucoming.sessions.model.Session;
import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import com.wafflestudio.areucoming.sessions.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/sessions", "/sessions"})
public class SessionController {
    private final SessionService sessionService;

    /**
     * 세션 생성(요청)
     * - userId 기준으로 couple_id를 찾아 status = PENDING 세션 생성
     */
    @PostMapping("")
    public ResponseEntity<Session> create(@RequestBody CreateSessionRequest req) {
        Session created = sessionService.createSessionRequest(req.getUserId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 내가 속한 커플의 세션 목록
     */
    @GetMapping("")
    public ResponseEntity<List<Session>> list(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(sessionService.listSessionsForUser(userId));
    }

    /**
     * 현재 세션 상태 확인
     */
    @GetMapping("/{sessionId}/status")
    public ResponseEntity<SessionStatusResponse> status(
            @PathVariable Long sessionId,
            @RequestParam("userId") Long userId
    ) {
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
    public ResponseEntity<Session> accept(@PathVariable Long sessionId, @RequestBody AcceptSessionRequest req) {
        return ResponseEntity.ok(sessionService.acceptSession(sessionId, req.getUserId()));
    }

    /**
     * 만남 기록 버튼 (세션 자체 meet_at/meet_lat/meet_lng 저장 + history에도 MEET_DONE 포인트)
     */
    @PostMapping("/{sessionId}/meetings")
    public ResponseEntity<Session> confirmMeet(@PathVariable Long sessionId, @RequestBody MeetConfirmRequest req) {
        return ResponseEntity.ok(sessionService.confirmMeet(sessionId, req.getUserId(), req.getLat(), req.getLng()));
    }

    /**
     * 세션 종료 (ACTIVE/PENDING -> DONE)
     */
    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<Session> finish(@PathVariable Long sessionId, @RequestBody FinishSessionRequest req) {
        return ResponseEntity.ok(sessionService.cancelOrFinish(sessionId, req.getUserId(), req.getReason()));
    }

    /**
     * (웹소켓이 없을 때) 세션 포인트 저장용 REST
     * - type: POINT or MEMO
     * - MEMO면 text 필요
     * - lat/lng는 둘 다 넣게(권장)
     */
    @PostMapping("/{sessionId}/points")
    public ResponseEntity<SessionPoint> addPoint(
            @PathVariable Long sessionId,
            @RequestBody CreateSessionPointRequest req
    ) {
        return new ResponseEntity<>(
                sessionService.addSessionPoint(
                        sessionId,
                        req.getUserId(),
                        req.getType(),
                        req.getLat(),
                        req.getLng(),
                        req.getText()
                ),
                HttpStatus.CREATED
        );
    }

    /**
     * 사진 업로드 (multipart/form-data)
     * - file: 사진
     * - userId: 업로더
     */
    @PostMapping(value = "/{sessionId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SessionPoint> uploadPhoto(
            @PathVariable Long sessionId,
            @RequestParam("userId") Long userId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng
    ) {
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
            @RequestParam("userId") Long userId,
            @RequestParam(value = "sinceId", required = false) Long sinceId,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        List<SessionPoint> points = sessionService.getHistory(sessionId, userId, sinceId, limit);
        return ResponseEntity.ok(new HistoryResponse(sessionId, points));
    }
}