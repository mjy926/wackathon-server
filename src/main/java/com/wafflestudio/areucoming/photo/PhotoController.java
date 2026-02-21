package com.wafflestudio.areucoming.photo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/photo/image-upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = photoService.saveFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}
