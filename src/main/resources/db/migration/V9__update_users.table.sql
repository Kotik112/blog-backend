-- V2__update_users_table.sql

ALTER TABLE users
    ADD COLUMN email VARCHAR(255) NOT NULL UNIQUE,
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN last_name VARCHAR(100),
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN profile_picture_url VARCHAR(500),
    ADD COLUMN created_at TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN last_login_at TIMESTAMP;