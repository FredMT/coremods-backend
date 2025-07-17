-- Create comments table
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    commentable_type VARCHAR(10) NOT NULL,
    commentable_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments (id)
);

-- Create indexes for performance
CREATE INDEX idx_comments_commentable ON comments (commentable_type, commentable_id);
CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comments_parent_id ON comments (parent_id);
CREATE INDEX idx_comments_created_at ON comments (created_at);
CREATE INDEX idx_comments_deleted_at ON comments (deleted_at);

-- Add constraint to ensure commentable_type is valid
ALTER TABLE comments ADD CONSTRAINT chk_commentable_type CHECK (commentable_type IN ('mod', 'image', 'collection')); 