package com.wafflestudio.areucoming.sessions.repository;

import com.wafflestudio.areucoming.sessions.model.SessionPoint;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionPointRepository extends CrudRepository<SessionPoint, Long> {

    @Query("SELECT * FROM session_points WHERE session_id = :sessionId ORDER BY created_at ASC")
    List<SessionPoint> findAllBySessionId(Long sessionId);

    @Query("SELECT * FROM session_points WHERE session_id = :sessionId AND id > :sinceId ORDER BY created_at ASC")
    List<SessionPoint> findAllBySessionIdSince(Long sessionId, Long sinceId);

    @Query("SELECT * FROM session_points WHERE session_id = :sessionId AND user_id = :userId")
    List<SessionPoint> findAllByUserIdWithSessionId(Long userId, Long sessionId);
}