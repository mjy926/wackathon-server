-- V1__init.sql
SET NAMES utf8mb4;
SET time_zone = '+09:00'; -- KST (DATETIME 기본값/함수 사용 시 기준)

-- ------------------------------------------------------------
-- users
-- ------------------------------------------------------------
CREATE TABLE users (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  email         VARCHAR(255) NOT NULL,
  display_name  VARCHAR(255) NOT NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- couples
-- ------------------------------------------------------------
CREATE TABLE couples (
  id          BIGINT NOT NULL AUTO_INCREMENT,
  user1_id    BIGINT NOT NULL,
  user2_id    BIGINT NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),

  UNIQUE KEY uq_couples_user1_user2 (user1_id, user2_id),

  KEY idx_couples_user1 (user1_id),
  KEY idx_couples_user2 (user2_id),

  CONSTRAINT fk_couples_user1
    FOREIGN KEY (user1_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_couples_user2
    FOREIGN KEY (user2_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- invites
-- ------------------------------------------------------------
CREATE TABLE invites (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  inviter_user_id  BIGINT NOT NULL,
  code             VARCHAR(64) NOT NULL,
  expires_at       DATETIME NULL,
  used_at          DATETIME NULL,
  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_invites_code (code),
  KEY idx_invites_inviter_user (inviter_user_id),
  CONSTRAINT fk_invites_inviter_user
    FOREIGN KEY (inviter_user_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- sessions
-- ------------------------------------------------------------
CREATE TABLE sessions (
  id              BIGINT NOT NULL AUTO_INCREMENT,
  couple_id        BIGINT NOT NULL,

  request_user_id  BIGINT NOT NULL,
  requested_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status           VARCHAR(16) NOT NULL, -- PENDING | ACTIVE | DONE

  start_at         DATETIME NULL,
  end_at           DATETIME NULL,
  end_reason       VARCHAR(32) NULL, -- MEET_CONFIRMED | TIMEOUT | MANUAL_CANCEL

  meet_at          DATETIME NULL,
  meet_lat         DECIMAL(10,7) NULL,
  meet_lng         DECIMAL(10,7) NULL,

  PRIMARY KEY (id),

  KEY idx_sessions_couple (couple_id),
  KEY idx_sessions_request_user (request_user_id),
  KEY idx_sessions_status (status),
  KEY idx_sessions_requested_at (requested_at),

  CONSTRAINT fk_sessions_couple
    FOREIGN KEY (couple_id) REFERENCES couples(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,

  CONSTRAINT fk_sessions_request_user
    FOREIGN KEY (request_user_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- session_points (was session_contents)
-- ------------------------------------------------------------
CREATE TABLE session_points (
  id          BIGINT NOT NULL AUTO_INCREMENT,
  session_id  BIGINT NOT NULL,
  user_id     BIGINT NOT NULL,
  type        VARCHAR(16) NOT NULL, -- PHOTO | MEMO | MEET_DONE | POINT
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  lat         DECIMAL(10,7) NULL,
  lng         DECIMAL(10,7) NULL,

  photo_path  VARCHAR(1024) NULL,
  text        TEXT NULL,

  PRIMARY KEY (id),

  KEY idx_session_points_session (session_id),
  KEY idx_session_points_user (user_id),
  KEY idx_session_points_created_at (created_at),

  CONSTRAINT fk_session_points_session
    FOREIGN KEY (session_id) REFERENCES sessions(id)
    ON DELETE CASCADE ON UPDATE CASCADE,

  CONSTRAINT fk_session_points_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;