-- Create images table
CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    game_mod_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    ext VARCHAR(50) NOT NULL,
    image_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    width INTEGER,
    height INTEGER,
    display_order INTEGER,
    created_time TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_images_game_mod FOREIGN KEY (game_mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

-- Create youtube_videos table
CREATE TABLE youtube_videos (
    id BIGSERIAL PRIMARY KEY,
    game_mod_id BIGINT NOT NULL,
    youtube_url VARCHAR(500) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INTEGER,
    created_time TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_youtube_videos_game_mod FOREIGN KEY (game_mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_images_game_mod_id ON images (game_mod_id);
CREATE INDEX idx_images_image_type ON images (image_type);
CREATE INDEX idx_images_display_order ON images (display_order);

CREATE INDEX idx_youtube_videos_game_mod_id ON youtube_videos (game_mod_id);
CREATE INDEX idx_youtube_videos_display_order ON youtube_videos (display_order);

-- Add constraints for image types
ALTER TABLE images ADD CONSTRAINT chk_image_type CHECK (image_type IN ('HEADER', 'MOD_IMAGE'));

-- Add constraint to ensure only one header image per mod
CREATE UNIQUE INDEX idx_unique_header_image ON images (game_mod_id) WHERE image_type = 'HEADER'; 