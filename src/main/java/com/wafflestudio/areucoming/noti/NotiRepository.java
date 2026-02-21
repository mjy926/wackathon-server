package com.wafflestudio.areucoming.noti;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/* TODO : noti entity 가 따로 없으므로 아래 메서드들은 user, partner repo로 추후 이동해야 할 필요 있음. */

@Repository
@RequiredArgsConstructor
public class NotiRepository {

    private final JdbcTemplate jdbcTemplate;

    // Save FCM token into users.token
    public int updateUserToken(long userId, String token) {
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        return jdbcTemplate.update(sql, token, userId);
    }

    // Find partner id from couples table
    public Optional<Long> findPartnerUserId(long userId) {
        String sql =
                "SELECT CASE " +
                        "  WHEN user1_id = ? THEN user2_id " +
                        "  ELSE user1_id " +
                        "END AS partner_id " +
                        "FROM couples " +
                        "WHERE user1_id = ? OR user2_id = ? " +
                        "LIMIT 1";

        try {
            Long partnerId =
                    jdbcTemplate.queryForObject(sql, Long.class, userId, userId, userId);
            return Optional.ofNullable(partnerId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findUserToken(long userId) {
        String sql = "SELECT token FROM users WHERE id = ? LIMIT 1";
        try {
            String token = jdbcTemplate.queryForObject(sql, String.class, userId);
            if (token == null || token.isBlank()) return Optional.empty();
            return Optional.of(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findUserDisplayName(long userId) {
        String sql = "SELECT display_name FROM users WHERE id = ? LIMIT 1";
        try {
            String name = jdbcTemplate.queryForObject(sql, String.class, userId);
            return Optional.ofNullable(name);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}