package com.wafflestudio.areucoming.couples.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("couples")
public class Couples {
    @Id
    Long id;

    @Column("user1_id")
    Long user1Id;

    @Column("user2_id")
    Long user2Id;

    @CreatedDate
    @Column("created_at")
    LocalDateTime createdAt;
}
