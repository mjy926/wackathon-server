package com.wafflestudio.areucoming.noti;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.users.service.UserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotiController {
    private final NotiService notiService;
    private final UserService userService;

    public record RegisterTokenRequest(String token) {}

    /**
     * Client registers its FCM token.
    **/
    @PostMapping("/token")
    public ResponseEntity<?> registerToken(
            @AuthenticationPrincipal String email,
            @RequestBody RegisterTokenRequest request
    ) {
        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }
            if (request == null) {
                throw new IllegalArgumentException("Request body is required");
            }
            UserDto userDto = userService.getCurrentUser(email);
            notiService.registerUserToken(userDto.getId(), request.token());
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
    public ResponseEntity<?> sendToPartner(@AuthenticationPrincipal String email) {
        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }
            UserDto userDto = userService.getCurrentUser(email);
            String messageId =
                    notiService.notifyLocationShareEnabled(userDto.getId(), userDto.getNickname());
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
