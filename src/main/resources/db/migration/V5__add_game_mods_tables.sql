CREATE TABLE game_mods (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    mod_type VARCHAR(255) NOT NULL,
    category_id BIGINT,
    suggested_category VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    language VARCHAR(50),
    version VARCHAR(50) NOT NULL,
    author_id BIGINT NOT NULL,
    overview VARCHAR(350),
    description TEXT NOT NULL,
    has_nudity BOOLEAN DEFAULT FALSE,
    has_skimpy_outfits BOOLEAN DEFAULT FALSE,
    has_extreme_violence BOOLEAN DEFAULT FALSE,
    is_sexualized BOOLEAN DEFAULT FALSE,
    has_profanity BOOLEAN DEFAULT FALSE,
    is_character_preset BOOLEAN DEFAULT FALSE,
    has_real_world_references BOOLEAN DEFAULT FALSE,
    includes_visual_preset BOOLEAN DEFAULT FALSE,
    has_save_files BOOLEAN DEFAULT FALSE,
    has_translation_files BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_game_mods_game FOREIGN KEY (game_id) REFERENCES igdb_games (id),
    CONSTRAINT fk_game_mods_category FOREIGN KEY (category_id) REFERENCES game_mod_categories (id),
    CONSTRAINT fk_game_mods_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE INDEX idx_game_mods_game_id ON game_mods (game_id);
CREATE INDEX idx_game_mods_author_id ON game_mods (author_id);
CREATE INDEX idx_game_mods_status ON game_mods (status);

INSERT INTO game_mod_categories (game_id, category_name, approved)
SELECT id, 'Miscellaneous', TRUE FROM igdb_games
ON CONFLICT DO NOTHING; 