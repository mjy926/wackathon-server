-- V2__add_user_token.sql
ALTER TABLE users
    ADD COLUMN token VARCHAR(512) NULL;
