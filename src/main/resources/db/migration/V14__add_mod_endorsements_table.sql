-- Create mod_endorsements table
CREATE TABLE mod_endorsements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mod_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_mod_endorsements_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_mod_endorsements_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,
    CONSTRAINT uk_mod_endorsements_user_mod UNIQUE (user_id, mod_id)
);

-- Create indexes for performance
CREATE INDEX idx_mod_endorsements_user_id ON mod_endorsements (user_id);
CREATE INDEX idx_mod_endorsements_mod_id ON mod_endorsements (mod_id); 