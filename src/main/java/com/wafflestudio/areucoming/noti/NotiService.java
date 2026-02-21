package com.wafflestudio.areucoming.noti;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotiService {

    private static final Logger log = LoggerFactory.getLogger(NotiService.class);

    private final NotiRepository notiRepository;


    /**
     * Register user's FCM token
     */
    public void registerUserToken(long userId, String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }

        int updated = notiRepository.updateUserToken(userId, token);

        if (updated == 0) {
            throw new IllegalStateException("User not found");
        }

        log.info("Saved token for userId={}, prefix={}",
                userId, token.substring(0, Math.min(12, token.length())) + "...");
    }

    /**
     * Send location-share enabled notification to partner
     */
    public String notifyLocationShareEnabled(long requesterUserId)
            throws FirebaseMessagingException {

        Long partnerUserId = notiRepository.findPartnerUserId(requesterUserId)
                .orElseThrow(() ->
                        new IllegalStateException("Partner not found"));

        String partnerToken = notiRepository.findUserToken(partnerUserId)
                .orElseThrow(() ->
                        new IllegalStateException("Partner token not found"));

        String requesterName =
                notiRepository.findUserDisplayName(requesterUserId)
                        .orElse("상대");

        String title = "위치공유";
        String body = requesterName + "님께서 위치공유를 활성화했습니다.";


        /* 알림 클릭 이벤트 후 js에서 파싱할 파트너 데이터 */
        Map<String, String> data = new HashMap<>();
        data.put("type", "LOCATION_SHARE_ENABLED");
        data.put("requesterUserId",
                String.valueOf(requesterUserId));

        Message.Builder builder = Message.builder()
                .setToken(partnerToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (!data.isEmpty()) {
            builder.putAllData(data);
        }

        Message message = builder.build();

        String response =
                FirebaseMessaging.getInstance().send(message);

        log.info("Push sent. messageId={}", response);

        return response;
    }
}