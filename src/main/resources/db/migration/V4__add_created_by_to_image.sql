-- Add created_by FK to image
ALTER TABLE image
    ADD COLUMN created_by BIGINT NOT NULL DEFAULT 1;

ALTER TABLE image
    ADD CONSTRAINT fk_image_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE CASCADE;

-- Remove default after adding the constraint to make it required for new records
ALTER TABLE image
    ALTER COLUMN created_by DROP DEFAULT;