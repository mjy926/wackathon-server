package com.wafflestudio.areucoming.sessions.repository;

import com.wafflestudio.areucoming.sessions.model.Session;
import com.wafflestudio.areucoming.sessions.model.SessionStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, Long> {

    @Query("SELECT * FROM sessions WHERE couple_id = :coupleId ORDER BY requested_at DESC")
    List<Session> findAllByCoupleId(Long coupleId);

    @Query("SELECT * FROM sessions WHERE couple_id = :coupleId AND status IN (:s1, :s2) ORDER BY requested_at DESC LIMIT 1")
    Optional<Session> findLatestByCoupleIdAndStatusIn(Long coupleId, SessionStatus s1, SessionStatus s2);

    @Query("SELECT COUNT(*) FROM sessions WHERE requested_at >= DATE_FORMAT(NOW(), '%Y-%m-01 00:00:00') AND requested_at <= LAST_DAY(NOW()) AND couple_id = :coupleId")
    int countSessionsThisMonth(Long coupleId);
}