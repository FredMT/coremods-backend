-- Create mod_tags table
CREATE TABLE mod_tags (
    id BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL,
    tag VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_mod_tags_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,
    CONSTRAINT fk_mod_tags_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_mod_tags_mod_tag UNIQUE (mod_id, tag)
);

-- Create mod_tag_votes table
CREATE TABLE mod_tag_votes (
    id BIGSERIAL PRIMARY KEY,
    mod_tag_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_mod_tag_votes_mod_tag FOREIGN KEY (mod_tag_id) REFERENCES mod_tags (id) ON DELETE CASCADE,
    CONSTRAINT fk_mod_tag_votes_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_mod_tag_votes_mod_tag_user UNIQUE (mod_tag_id, user_id)
);

-- Create indexes for performance
CREATE INDEX idx_mod_tags_mod_id ON mod_tags (mod_id);
CREATE INDEX idx_mod_tags_user_id ON mod_tags (user_id);
CREATE INDEX idx_mod_tags_tag ON mod_tags (tag);
CREATE INDEX idx_mod_tag_votes_mod_tag_id ON mod_tag_votes (mod_tag_id);
CREATE INDEX idx_mod_tag_votes_user_id ON mod_tag_votes (user_id); 