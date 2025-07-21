-- Remove unused columns from igdb_games
ALTER TABLE igdb_games DROP COLUMN IF EXISTS cover_url;

-- Add new columns to igdb_games
ALTER TABLE igdb_games ADD COLUMN cover_image_id VARCHAR(255);
ALTER TABLE igdb_games ADD COLUMN slug VARCHAR(255);
ALTER TABLE igdb_games ADD COLUMN updated_at_igdb BIGINT;

-- Remove timestamps from igdb_platforms (keep only id and name)
ALTER TABLE igdb_platforms DROP COLUMN IF EXISTS created_at;
ALTER TABLE igdb_platforms DROP COLUMN IF EXISTS updated_at;

-- Drop existing many-to-many table
DROP TABLE IF EXISTS igdb_games_platforms;

-- Create new linking table
CREATE TABLE igdb_game_platforms (
    game_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    PRIMARY KEY (game_id, platform_id),
    FOREIGN KEY (game_id) REFERENCES igdb_games(id) ON DELETE CASCADE,
    FOREIGN KEY (platform_id) REFERENCES igdb_platforms(id) ON DELETE CASCADE
);

-- Add indexes for performance
CREATE INDEX idx_game_platforms_game_id ON igdb_game_platforms(game_id);
CREATE INDEX idx_game_platforms_platform_id ON igdb_game_platforms(platform_id);

-- Add index on igdb_games slug for better query performance
CREATE INDEX idx_igdb_games_slug ON igdb_games(slug); 