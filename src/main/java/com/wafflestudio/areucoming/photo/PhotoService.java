package com.wafflestudio.areucoming.photo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class PhotoService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(Long userId, MultipartFile multipartFile) throws IOException {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효한 userId가 필요합니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String safeFilename = (originalFilename == null || originalFilename.isBlank())
                ? "unknown"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String key = "images/user-" + userId + "/" + UUID.randomUUID() + "_" + safeFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, key, multipartFile.getInputStream(), metadata);
        amazonS3.setObjectAcl(bucket, key, CannedAccessControlList.PublicRead);

        return amazonS3.getUrl(bucket, key).toString();
    }
}
