package com.wafflestudio.areucoming.users.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("users")
public class User {
    @Id
    Long id;

    String email;

    @Column("display_name")
    String displayName;

    @CreatedDate
    @Column("created_at")
    LocalDateTime createdAt;

    String password;
    String token;
}
