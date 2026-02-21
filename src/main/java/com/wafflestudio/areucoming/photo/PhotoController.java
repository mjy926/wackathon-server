package com.wafflestudio.areucoming.photo;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PhotoController {
    private final PhotoService photoService;
    private  final UserService userService;

    /* 테스트용 엔드포인트. 업로드 완료 정상적인 처리 확인. */
    @PostMapping(value = "/photo/image-upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(@AuthenticationPrincipal String email,
            @RequestParam("file") MultipartFile file) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }
        UserDto dto = userService.getCurrentUser(email);
        try {
            String imageUrl = photoService.saveFile(dto.getId(), file);
            return ResponseEntity.ok(imageUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}
