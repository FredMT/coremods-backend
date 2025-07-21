CREATE TABLE igdb_game_dlcs (
    id BIGSERIAL PRIMARY KEY,
    parent_game_id BIGINT NOT NULL,
    dlc_id BIGINT NOT NULL,
    
    CONSTRAINT fk_igdb_game_dlcs_parent_game FOREIGN KEY (parent_game_id) REFERENCES igdb_games (id) ON DELETE CASCADE,
    CONSTRAINT uk_igdb_game_dlcs_parent_dlc UNIQUE (parent_game_id, dlc_id)
);

CREATE INDEX idx_igdb_game_dlcs_parent_game_id ON igdb_game_dlcs (parent_game_id);

CREATE INDEX idx_igdb_game_dlcs_dlc_id ON igdb_game_dlcs (dlc_id); 