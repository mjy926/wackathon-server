package com.wafflestudio.areucoming.sessions.service;

import com.wafflestudio.areucoming.couples.model.Couples;
import com.wafflestudio.areucoming.couples.service.CouplesService;
import com.wafflestudio.areucoming.sessions.model.*;
import com.wafflestudio.areucoming.sessions.repository.SessionPointRepository;
import com.wafflestudio.areucoming.sessions.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionPointRepository sessionPointRepository;
    private final CouplesService couplesService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public void assertCanAccess(Session session, Long userId) {
        Couples couple = couplesService.getCoupleByUserIdOrThrow(userId);
        if (!Objects.equals(couple.getId(), session.getCoupleId())) {
            throw new ResponseStatusException(FORBIDDEN, "User is not in this session's couple");
        }
    }

    public Session createSessionRequest(Long userId) {
        Couples couple = couplesService.getCoupleByUserIdOrThrow(userId);

        // 한 커플에 동시에 진행 중/대기 중 세션은 1개만 (원하면 정책 바꾸면 됨)
        Optional<Session> existing = sessionRepository.findLatestByCoupleIdAndStatusIn(
                couple.getId(), SessionStatus.PENDING, SessionStatus.ACTIVE
        );
        if (existing.isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Session already exists (PENDING/ACTIVE)");
        }

        Session toSave = Session.builder()
                .coupleId(couple.getId())
                .requestUserId(userId)
                .requestedAt(LocalDateTime.now())
                .status(SessionStatus.PENDING)
                .build();
        return sessionRepository.save(toSave);
    }

    public Session acceptSession(Long sessionId, Long userId) {
        Session session = getSessionOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.PENDING) {
            throw new ResponseStatusException(BAD_REQUEST, "Session is not PENDING");
        }
        assertCanAccess(session, userId);
        if (Objects.equals(session.getRequestUserId(), userId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester cannot accept");
        }

        Session updated = Session.builder()
                .id(session.getId())
                .coupleId(session.getCoupleId())
                .requestUserId(session.getRequestUserId())
                .requestedAt(session.getRequestedAt())
                .status(SessionStatus.ACTIVE)
                .startAt(LocalDateTime.now())
                .endAt(session.getEndAt())
                .endReason(session.getEndReason())
                .meetAt(session.getMeetAt())
                .meetLat(session.getMeetLat())
                .meetLng(session.getMeetLng())
                .build();
        return sessionRepository.save(updated);
    }

    public Session cancelOrFinish(Long sessionId, Long userId, EndReason reason) {
        Session session = getSessionOrThrow(sessionId);
        assertCanAccess(session, userId);
        if (session.getStatus() == SessionStatus.DONE) {
            return session;
        }

        EndReason finalReason = (reason == null) ? EndReason.MANUAL_CANCEL : reason;
        LocalDateTime now = LocalDateTime.now();
        Session updated = Session.builder()
                .id(session.getId())
                .coupleId(session.getCoupleId())
                .requestUserId(session.getRequestUserId())
                .requestedAt(session.getRequestedAt())
                .status(SessionStatus.DONE)
                .startAt(session.getStartAt())
                .endAt(now)
                .endReason(finalReason)
                .meetAt(session.getMeetAt())
                .meetLat(session.getMeetLat())
                .meetLng(session.getMeetLng())
                .build();
        return sessionRepository.save(updated);
    }

    public Session confirmMeet(Long sessionId, Long userId, BigDecimal lat, BigDecimal lng) {
        Session session = getSessionOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ResponseStatusException(BAD_REQUEST, "Session is not ACTIVE");
        }
        assertCanAccess(session, userId);
        if (lat == null || lng == null) throw new ResponseStatusException(BAD_REQUEST, "lat/lng are required");

        LocalDateTime now = LocalDateTime.now();
        Session updated = Session.builder()
                .id(session.getId())
                .coupleId(session.getCoupleId())
                .requestUserId(session.getRequestUserId())
                .requestedAt(session.getRequestedAt())
                .status(SessionStatus.DONE)
                .startAt(session.getStartAt())
                .endAt(session.getEndAt())
                .endReason(session.getEndReason())
                .meetAt(now)
                .meetLat(lat)
                .meetLng(lng)
                .build();
        Session saved = sessionRepository.save(updated);

        // history에도 남기기
        sessionPointRepository.save(SessionPoint.builder()
                .sessionId(sessionId)
                .userId(userId)
                .type(SessionPointType.MEET_DONE)
                .createdAt(now)
                .lat(lat)
                .lng(lng)
                .build());

        return saved;
    }

    public SessionPoint addSessionPoint(
            Long sessionId,
            Long userId,
            SessionPointType type,
            BigDecimal lat,
            BigDecimal lng,
            String text
    ) {
        if (type == null) throw new ResponseStatusException(BAD_REQUEST, "type is required");
        if (type != SessionPointType.POINT && type != SessionPointType.MEMO) {
            throw new ResponseStatusException(BAD_REQUEST, "type must be POINT or MEMO");
        }
        if (lat == null || lng == null) {
            throw new ResponseStatusException(BAD_REQUEST, "lat/lng are required");
        }
        if (type == SessionPointType.MEMO && !StringUtils.hasText(text)) {
            throw new ResponseStatusException(BAD_REQUEST, "text is required for MEMO");
        }

        Session session = getSessionOrThrow(sessionId);
        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ResponseStatusException(BAD_REQUEST, "Session is not ACTIVE");
        }
        assertCanAccess(session, userId);

        return sessionPointRepository.save(SessionPoint.builder()
                .sessionId(sessionId)
                .userId(userId)
                .type(type)
                .createdAt(LocalDateTime.now())
                .lat(lat)
                .lng(lng)
                .text(type == SessionPointType.MEMO ? text : null)
                .build());
    }

    public SessionPoint uploadPhoto(Long sessionId, Long userId, MultipartFile file, BigDecimal lat, BigDecimal lng) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "file is required");
        }

        if (lat == null || lng == null) {
            throw new ResponseStatusException(BAD_REQUEST, "lat/lng are required");
        }

        Session session = getSessionOrThrow(sessionId);
        assertCanAccess(session, userId);

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("photo");
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);
        String filename = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir, "sessions", String.valueOf(sessionId));
        try {
            Files.createDirectories(dir);
            Path saved = dir.resolve(filename);
            file.transferTo(saved);
            String photoPath = dir.resolve(filename).toString().replace("\\\\", "/");

            return sessionPointRepository.save(SessionPoint.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .type(SessionPointType.PHOTO)
                    .createdAt(LocalDateTime.now())
                    .lat(lat)
                    .lng(lng)
                    .photoPath(photoPath)
                    .build());
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to save file");
        }
    }

    public List<SessionPoint> getHistory(Long sessionId, Long userId, Long sinceId, Integer limit) {
        Session session = getSessionOrThrow(sessionId);
        assertCanAccess(session, userId);

        int l = (limit == null) ? 500 : Math.min(Math.max(limit, 1), 2000);
        if (sinceId == null) {
            List<SessionPoint> points = sessionPointRepository.findAllBySessionId(sessionId);
            if (points.size() <= l) return points;
            return points.subList(Math.max(points.size() - l, 0), points.size());
        }
        List<SessionPoint> points = sessionPointRepository.findAllBySessionIdSince(sessionId, sinceId);
        if (points.size() <= l) return points;
        return points.subList(0, l);
    }

    public Session getSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Session not found"));
    }

    public List<Session> listSessionsForUser(Long userId) {
        Couples couple = couplesService.getCoupleByUserIdOrThrow(userId);
        return sessionRepository.findAllByCoupleId(couple.getId());
    }
}