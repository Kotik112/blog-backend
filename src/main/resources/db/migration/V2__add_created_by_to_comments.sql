-- Add user_id FK to comment
ALTER TABLE comment
    ADD COLUMN user_id BIGINT NOT NULL;

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE;
