package com.wafflestudio.areucoming.users.controller;

import com.wafflestudio.areucoming.auth.dto.UserDto;
import com.wafflestudio.areucoming.photo.PhotoService;
import com.wafflestudio.areucoming.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;
    private final PhotoService photoService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal String email) {
        UserDto dto = userService.getCurrentUser(email);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    @PostMapping(value = "/profile-image/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal String email,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (email == null || email.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }
            Long userId = userService.getCurrentUserId(email);
            String imageUrl = photoService.saveFile(userId, file);
            userService.updateProfileImageUrl(email, imageUrl);
            return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 업로드 중 오류가 발생했습니다."));
        }
    }
}
