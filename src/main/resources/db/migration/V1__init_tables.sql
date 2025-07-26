-- Create blog_post table
CREATE TABLE IF NOT EXISTS blog_post (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_edited_at TIMESTAMPTZ,
    is_edited BOOLEAN DEFAULT FALSE
);

-- Create comment table
CREATE TABLE IF NOT EXISTS comment (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_edited_at TIMESTAMPTZ,
    is_edited BOOLEAN DEFAULT FALSE,
    blog_post_id BIGINT REFERENCES blog_post(id) ON DELETE CASCADE
);

-- Create like table
CREATE TABLE IF NOT EXISTS "like" (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    blog_post_id BIGINT REFERENCES blog_post(id) ON DELETE CASCADE
);

-- Create image table
CREATE TABLE IF NOT EXISTS image (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    image_data BYTEA NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Add image_id to blog_post and set FK
ALTER TABLE blog_post
    ADD COLUMN image_id BIGINT;

ALTER TABLE blog_post
    ADD CONSTRAINT fk_blog_post_image_id
        FOREIGN KEY (image_id)
        REFERENCES image(id);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_picture_url VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Add user_id FK to blog_post
ALTER TABLE blog_post
    ADD COLUMN user_id BIGINT;

ALTER TABLE blog_post
    ADD CONSTRAINT fk_blog_post_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id);

-- Create contact_status enum
CREATE TYPE contact_status AS ENUM ('PENDING', 'RESOLVED', 'IGNORED');

-- Create contact_us_tbl
CREATE TABLE contact_us_tbl (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    user_agent TEXT,
    internal_note TEXT
);
