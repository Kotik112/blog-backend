-- Add created_by column to blog_post table
ALTER TABLE blog_post
ADD COLUMN created_by BIGINT;

-- Add the foreign key constraint to link to user table
ALTER TABLE blog_post
ADD CONSTRAINT fk_blog_post_created_by
FOREIGN KEY (created_by) REFERENCES users(id);