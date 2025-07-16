-- Add identifier column
ALTER TABLE youtube_videos ADD COLUMN identifier VARCHAR(50);

-- Make identifier column NOT NULL (after data migration)
ALTER TABLE youtube_videos ALTER COLUMN identifier SET NOT NULL;

-- Drop the old youtube_url column
ALTER TABLE youtube_videos DROP COLUMN youtube_url;

-- Add index for identifier column for performance
CREATE INDEX idx_youtube_videos_identifier ON youtube_videos (identifier);

-- Add constraint to ensure identifier is unique per game mod
CREATE UNIQUE INDEX idx_unique_youtube_identifier_per_mod ON youtube_videos (game_mod_id, identifier); 