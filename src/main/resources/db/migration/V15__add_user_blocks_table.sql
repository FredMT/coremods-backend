-- Create user_blocks table
CREATE TABLE user_blocks (
    id BIGSERIAL PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    scope_type VARCHAR(50) NOT NULL,
    mod_id BIGINT NULL,
    blocked_author_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_blocks_blocker FOREIGN KEY (blocker_id) REFERENCES users (id),
    CONSTRAINT fk_user_blocks_blocked FOREIGN KEY (blocked_id) REFERENCES users (id),
    CONSTRAINT fk_user_blocks_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_blocks_blocked_author FOREIGN KEY (blocked_author_id) REFERENCES users (id),
    CONSTRAINT ck_user_blocks_scope_validation CHECK (
        (scope_type = 'MOD' AND mod_id IS NOT NULL AND blocked_author_id IS NULL) OR
        (scope_type = 'AUTHOR_GLOBAL' AND blocked_author_id IS NOT NULL AND mod_id IS NULL) OR
        (scope_type IN ('DIRECT_MESSAGES', 'INTERACTION') AND mod_id IS NULL AND blocked_author_id IS NULL)
    ),
    CONSTRAINT ck_user_blocks_not_self CHECK (blocker_id != blocked_id)
);

-- Create unique indexes for each scope type to prevent duplicates
CREATE UNIQUE INDEX uk_user_blocks_mod_scope ON user_blocks (blocker_id, blocked_id, mod_id) 
    WHERE scope_type = 'MOD';
    
CREATE UNIQUE INDEX uk_user_blocks_author_global_scope ON user_blocks (blocker_id, blocked_author_id) 
    WHERE scope_type = 'AUTHOR_GLOBAL';
    
CREATE UNIQUE INDEX uk_user_blocks_dm_scope ON user_blocks (blocker_id, blocked_id) 
    WHERE scope_type = 'DIRECT_MESSAGES';
    
CREATE UNIQUE INDEX uk_user_blocks_interaction_scope ON user_blocks (blocker_id, blocked_id) 
    WHERE scope_type = 'INTERACTION';

-- Create indexes for performance
CREATE INDEX idx_user_blocks_blocker_id ON user_blocks (blocker_id);
CREATE INDEX idx_user_blocks_blocked_id ON user_blocks (blocked_id);
CREATE INDEX idx_user_blocks_scope_type ON user_blocks (scope_type);
CREATE INDEX idx_user_blocks_mod_id ON user_blocks (mod_id);
CREATE INDEX idx_user_blocks_blocked_author_id ON user_blocks (blocked_author_id);

-- Create composite indexes for common queries
CREATE INDEX idx_user_blocks_blocker_scope ON user_blocks (blocker_id, scope_type);
CREATE INDEX idx_user_blocks_blocked_scope ON user_blocks (blocked_id, scope_type); 