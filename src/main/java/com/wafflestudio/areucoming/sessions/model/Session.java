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
@Table("sessions")
public class Session {
    @Id
    Long id;

    @Column("couple_id")
    Long coupleId;

    @Column("request_user_id")
    Long requestUserId;

    @CreatedDate
    @Column("requested_at")
    LocalDateTime requestedAt;

    SessionStatus status;

    @Column("start_at")
    LocalDateTime startAt;

    @Column("end_at")
    LocalDateTime endAt;

    @Column("end_reason")
    EndReason endReason;

    @Column("meet_at")
    LocalDateTime meetAt;

    @Column("meet_lat")
    BigDecimal meetLat;

    @Column("meet_lng")
    BigDecimal meetLng;
}