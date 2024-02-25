-- Create a database table for images
CREATE TABLE IF NOT EXISTS image (
     "id" SERIAL PRIMARY KEY,
     "name" VARCHAR(255) NOT NULL,
     "type" VARCHAR(50) NOT NULL,
     "image_data" BYTEA NOT NULL,
     "created_at" TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Add a new column image_id to blog_post
ALTER TABLE blog_post
    ADD COLUMN "image_id" BIGINT;

-- Add a foreign key constraint for image_id
ALTER TABLE blog_post
    ADD CONSTRAINT fk_blog_post_image_id
        FOREIGN KEY (image_id)
            REFERENCES image(id);