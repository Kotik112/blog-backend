-- Create a database table for blog posts
CREATE TABLE IF NOT EXISTS blog_post (
    "id" SERIAL PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL,
    "content" TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_edited_at TIMESTAMPTZ,
    is_edited BOOLEAN DEFAULT FALSE
);

-- Create a database table for blog post comments
CREATE TABLE IF NOT EXISTS comment (
    "id" SERIAL PRIMARY KEY,
    "content" TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_edited_at TIMESTAMPTZ,
    is_edited BOOLEAN DEFAULT FALSE,
    "blog_post_id" BIGINT REFERENCES blog_post(id) ON DELETE CASCADE
);

-- Create a database table for blog post likes
CREATE TABLE IF NOT EXISTS "like" (
    "id" SERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    "blog_post_id" BIGINT REFERENCES blog_post(id) ON DELETE CASCADE
);