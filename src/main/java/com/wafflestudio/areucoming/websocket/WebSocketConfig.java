package com.wafflestudio.areucoming.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionWsHandler sessionWsHandler;
    private final JwtQueryHandshakeInterceptor jwtQueryHandshakeInterceptor;

    public WebSocketConfig(
            SessionWsHandler sessionWsHandler,
            JwtQueryHandshakeInterceptor jwtQueryHandshakeInterceptor
    ) {
        this.sessionWsHandler = sessionWsHandler;
        this.jwtQueryHandshakeInterceptor = jwtQueryHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sessionWsHandler, "/ws/session")
                .addInterceptors(jwtQueryHandshakeInterceptor)
                .setAllowedOriginPatterns("*"); // TODO(운영): 프론트 도메인으로 제한
    }
}