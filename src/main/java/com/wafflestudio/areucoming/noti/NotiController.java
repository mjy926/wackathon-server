package com.wafflestudio.areucoming.noti;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotiController {
    private final NotiService notiService;

    /* TODO : 요청 스키마(임시) */
    public record RegisterTokenRequest(Long userId, String token) {}
    /* TODO : 요청 스키마(임시) */
    public record NotifyPartnerRequest(Long requesterUserId) {}

    /* TODO : jwt token을 받는 방식으로 수정 필요. */
    /**
     * Client registers its FCM token.
    **/
    @PostMapping("/token")
    public ResponseEntity<?> registerToken(
            @RequestBody RegisterTokenRequest request
    ) {
        try {
            if (request == null || request.userId() == null) {
                throw new IllegalArgumentException("userId is required");
            }
            notiService.registerUserToken(request.userId(), request.token());
            return ResponseEntity.ok(Map.of("message", "Token registered"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    /**
     * User requests to send a push notification to their partner.
     */
    @PostMapping("/partner")
    public ResponseEntity<?> sendToPartner(
            @RequestBody NotifyPartnerRequest request
    ) {
        try {
            if (request == null || request.requesterUserId() == null) {
                throw new IllegalArgumentException("requesterUserId is required");
            }
            String messageId =
                    notiService.notifyLocationShareEnabled(request.requesterUserId());
            return ResponseEntity.ok(Map.of("messageId", messageId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to send push: " + e.getMessage()));
        }
    }
}
