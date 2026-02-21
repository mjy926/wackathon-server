package com.wafflestudio.areucoming.sessions.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("session_points")
public class SessionPoint {
    @Id
    Long id;

    @Column("session_id")
    Long sessionId;

    @Column("user_id")
    Long userId;

    SessionPointType type;

    @CreatedDate
    @Column("created_at")
    LocalDateTime createdAt;

    BigDecimal lat;
    BigDecimal lng;

    @Column("photo_path")
    String photoPath;

    String text;
}