package com.wafflestudio.areucoming.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SessionWsHandler extends TextWebSocketHandler {

    private final ObjectMapper om = new ObjectMapper();

    // sessionId -> connected sockets
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    
    // 60초 주기 session point 저장을 위한 로그
    private final ConcurrentHashMap<String, Long> lastSavedAt = new ConcurrentHashMap<>();
    private static final long SAVE_INTERVAL_MS = 60_000;

    private boolean shouldSaveLocation(long sessionId, long userId, long nowMs) {
        String key = sessionId + ":" + userId;

        Long prev = lastSavedAt.get(key);
        if (prev == null || nowMs - prev >= SAVE_INTERVAL_MS) {
            lastSavedAt.put(key, nowMs);
            return true;
        }
        return false;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        long sessionId = (long) session.getAttributes().get(JwtQueryHandshakeInterceptor.ATTR_SESSION_ID);
        rooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        long sessionId = (long) session.getAttributes().get(JwtQueryHandshakeInterceptor.ATTR_SESSION_ID);
        long userId = (long) session.getAttributes().get(JwtQueryHandshakeInterceptor.ATTR_USER_ID);

        JsonNode root = om.readTree(message.getPayload());
        if (!(root instanceof ObjectNode obj)) {
            send(session, error("BAD_PAYLOAD", "payload must be JSON object"));
            return;
        }

        String type = obj.path("type").asText("");
        if (!type.equals("LOCATION") && !type.equals("POINT") && !type.equals("END")) {
            send(session, error("UNKNOWN_TYPE", "type must be LOCATION|POINT|END"));
            return;
        }

        // 서버가 신뢰 필드 덧씌움 (클라가 조작 못하게)
        obj.put("sessionId", sessionId);
        obj.put("userId", userId);
        if (!obj.hasNonNull("ts")) obj.put("ts", System.currentTimeMillis());

        // 위도, 경도 포함 여부 체크
        if (!type.equals("END")) {
            if (!obj.hasNonNull("lat") || !obj.hasNonNull("lng")) {
                send(session, error("MISSING_FIELD", "lat/lng required"));
                return;
            }
        }

        long now = System.currentTimeMillis();

        if (type.equals("LOCATION")) {
            if (shouldSaveLocation(sessionId, userId, now)) {
                double lat = obj.path("lat").asDouble();
                double lng = obj.path("lng").asDouble();

                // TODO(팀 구현): JDBC insert 호출
                // sessionPointService.insertTrack(sessionId, userId, lat, lng);
                // type은 'TRACK' 같은 걸 추천 (POINT랑 구분)
            }
        }

        // TODO(선택): POINT면 그냥 DB 저장(session_points INSERT)
        // if (type.equals("POINT")) { pointService.savePoint(sessionId, userId, ...); }

        String outbound = om.writeValueAsString(obj);

        // 같은 room 브로드캐스트(본인 포함). 본인 제외하려면 if (s != session) 추가.
        for (WebSocketSession s : rooms.getOrDefault(sessionId, Set.of())) {
            if (s.isOpen()) s.sendMessage(new TextMessage(outbound));
        }

        // END면 방 닫기(선택)
        if (type.equals("END")) {
            closeRoom(sessionId);
            clearSession(sessionId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        long sessionId = (long) session.getAttributes().get(JwtQueryHandshakeInterceptor.ATTR_SESSION_ID);
        Set<WebSocketSession> set = rooms.get(sessionId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) rooms.remove(sessionId);
        }
    }

    private void closeRoom(long sessionId) throws IOException {
        Set<WebSocketSession> set = rooms.remove(sessionId);
        if (set == null) return;
        for (WebSocketSession s : set) {
            if (s.isOpen()) s.close(CloseStatus.NORMAL);
        }
    }

    private void clearSession(long sessionId) {
        String prefix = sessionId + ":";
        lastSavedAt.keySet().removeIf(k -> k.startsWith(prefix));
    }

    private void send(WebSocketSession s, String json) throws IOException {
        s.sendMessage(new TextMessage(json));
    }

    private String error(String code, String msg) throws IOException {
        ObjectNode o = om.createObjectNode();
        o.put("type", "ERROR");
        o.put("code", code);
        o.put("msg", msg);
        return om.writeValueAsString(o);
    }
}