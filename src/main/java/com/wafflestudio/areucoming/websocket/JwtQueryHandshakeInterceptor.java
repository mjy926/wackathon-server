package com.wafflestudio.areucoming.websocket;

import java.util.Map;

import com.wafflestudio.areucoming.auth.service.AuthService;
import com.wafflestudio.areucoming.couples.repository.CouplesRepository;
import com.wafflestudio.areucoming.sessions.model.SessionStatus;
import com.wafflestudio.areucoming.sessions.repository.SessionRepository;
import com.wafflestudio.areucoming.users.repository.UserRepository;
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

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final CouplesRepository couplesRepository;

    public JwtQueryHandshakeInterceptor(
            AuthService authService,
            UserRepository userRepository,
            SessionRepository sessionRepository,
            CouplesRepository couplesRepository
    ) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.couplesRepository = couplesRepository;
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

        // 1) JWT 검증 + 유저 매핑
        if (!authService.validateAccessToken(token)) return false;
        String email = authService.getSub(token);
        if (email == null || email.isBlank()) return false;

        var user = userRepository.findByEmail(email);
        if (user == null || user.getId() == null) return false;
        final long userId = user.getId();

        // 2) 세션 조회
        var sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) return false;
        var sess = sessionOpt.get();

        // 3) 상태 체크:
        // - ACTIVE면 참가자 누구나 OK
        // - PENDING이면 생성자(requestUserId)만 OK
        var status = sess.getStatus();
        if (status == null) return false;

        if (status == SessionStatus.PENDING) {
            if (sess.getRequestUserId() == null || !sess.getRequestUserId().equals(userId)) return false;
        } else if (status != SessionStatus.ACTIVE) {
            return false;
        }

        // 4) 세션(커플)에 참여 중인지 판정
        Long coupleId = sessionOpt.get().getCoupleId();
        if (coupleId == null) return false;

        var coupleOpt = couplesRepository.findById(coupleId);
        if (coupleOpt.isEmpty()) return false;

        var couple = coupleOpt.get();
        boolean participant =
                (couple.getUser1Id() != null && couple.getUser1Id().equals(userId)) ||
                        (couple.getUser2Id() != null && couple.getUser2Id().equals(userId));

        if (!participant) return false;

        // 핸들러에서 쓰도록 저장
        attributes.put(ATTR_SESSION_ID, sessionId);
        attributes.put(ATTR_USER_ID, userId);

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {}
}
