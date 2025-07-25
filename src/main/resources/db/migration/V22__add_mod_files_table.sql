CREATE TABLE mod_files (
    id BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL REFERENCES game_mods(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    ext VARCHAR(10) NOT NULL,
    description TEXT,
    version VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    download_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_mod_files_mod_id ON mod_files(mod_id);
CREATE INDEX idx_mod_files_category ON mod_files(category); 