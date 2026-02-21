package com.wafflestudio.areucoming.websocket;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class JwtQueryHandshakeInterceptor implements HandshakeInterceptor {

    public static final String ATTR_SESSION_ID = "sessionId";
    public static final String ATTR_USER_ID = "userId";

    private final JwtVerifier jwtVerifier;
    private final SessionAuthJdbcRepository sessionAuthRepo;

    public JwtQueryHandshakeInterceptor(JwtVerifier jwtVerifier, SessionAuthJdbcRepository sessionAuthRepo) {
        this.jwtVerifier = jwtVerifier;
        this.sessionAuthRepo = sessionAuthRepo;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();

        // 도메인 포맷: wss://<domain>/ws/session?sessionId=123&token=<JWT>
        String sessionIdStr = params.getFirst("sessionId");
        String token = params.getFirst("token");

        if (sessionIdStr == null || token == null || token.isBlank()) return false;

        // 세션 id가 숫자인지 판정
        long sessionId;
        try {
            sessionId = Long.parseLong(sessionIdStr);
        } catch (NumberFormatException e) {
            return false;
        }

        // @TODO: JWT에 해당하는 유저가 존재하는지 판정
        final long userId;
        try {
            userId = jwtVerifier.verifyAndGetUserId(token);
        } catch (Exception e) {
            return false;
        }

        // @TODO: 그 유저가 이 세션에 참여하고 있는지 확인
        if (!sessionAuthRepo.isParticipant(sessionId, userId)) {
            return false;
        }

        // 핸들러에서 쓰도록 저장
        attributes.put(ATTR_SESSION_ID, sessionId);
        attributes.put(ATTR_USER_ID, userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {}
}
