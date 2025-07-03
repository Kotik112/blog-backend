-- Rename created_by column to user_id
ALTER TABLE blog_post
RENAME COLUMN created_by TO user_id;

-- Drop the old foreign key constraint
ALTER TABLE blog_post
DROP CONSTRAINT fk_blog_post_created_by;

-- Add a new foreign key constraint with the new column name
ALTER TABLE blog_post
ADD CONSTRAINT fk_blog_post_user_id
FOREIGN KEY (user_id) REFERENCES users(id);