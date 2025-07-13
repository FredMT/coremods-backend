CREATE TABLE game_mod_categories (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL,
    category_name VARCHAR(255) NOT NULL,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_game_mod_category_game FOREIGN KEY (game_id) REFERENCES igdb_games (id) ON DELETE CASCADE,
    CONSTRAINT uk_game_category UNIQUE (game_id, category_name)
); 