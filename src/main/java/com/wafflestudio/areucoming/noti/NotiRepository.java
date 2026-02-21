package com.wafflestudio.areucoming.noti;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotiRepository {

    private final JdbcTemplate jdbcTemplate;

    public int updateUserToken(long userId, String token) {
        String sql = "UPDATE users SET token = ? WHERE id = ?";
        return jdbcTemplate.update(sql, token, userId);
    }

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
            Long partnerId = jdbcTemplate.queryForObject(sql, Long.class, userId, userId, userId);
            return Optional.ofNullable(partnerId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findUserToken(long userId) {
        String sql = "SELECT token FROM users WHERE id = ? LIMIT 1";
        try {
            String token = jdbcTemplate.queryForObject(sql, String.class, userId);
            if (token == null || token.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(token);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
