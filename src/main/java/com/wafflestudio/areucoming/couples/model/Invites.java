package com.wafflestudio.areucoming.couples.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("invites")
public class Invites {
    @Id
    Long id;

    @Column("inviter_user_id")
    private Long inviterUserId;

    private String code;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("used_at")
    private LocalDateTime usedAt;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
}
