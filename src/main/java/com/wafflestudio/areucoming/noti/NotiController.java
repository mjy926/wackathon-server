package com.wafflestudio.areucoming.noti;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/noti")
@RequiredArgsConstructor
public class NotiController {
    private final NotiService notiService;


    /**
     * Client registers its FCM token.
    **/
    @PostMapping("/token")
    public ResponseEntity<?> registerToken(

    ) {
        // TODO: call notiService.registerToken(userId, req.token());
        return null;
    }



    /**
     * User requests to send a push notification to their partner.
     */
    @PostMapping("/partner")
    public ResponseEntity<?> sendToPartner(

    ) {
        return  null;
    }
}
