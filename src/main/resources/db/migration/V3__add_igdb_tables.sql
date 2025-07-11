CREATE TABLE igdb_games (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    summary TEXT,
    cover_url VARCHAR(255),
    release_date BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE igdb_platforms (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE igdb_games_platforms (
    game_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    PRIMARY KEY (game_id, platform_id),
    FOREIGN KEY (game_id) REFERENCES igdb_games(id),
    FOREIGN KEY (platform_id) REFERENCES igdb_platforms(id)
);

CREATE INDEX idx_igdb_games_name ON igdb_games(name);
CREATE INDEX idx_igdb_platforms_name ON igdb_platforms(name); 