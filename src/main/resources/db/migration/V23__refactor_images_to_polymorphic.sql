-- Add new polymorphic columns
ALTER TABLE images ADD COLUMN imageable_type VARCHAR(50);
ALTER TABLE images ADD COLUMN imageable_id BIGINT;
ALTER TABLE images ADD COLUMN storage_key VARCHAR(255);

-- Migrate existing data: convert game_mod_id to polymorphic relationship
UPDATE images 
SET imageable_type = 'MOD', 
    imageable_id = game_mod_id,
    storage_key = CONCAT(name, '.', ext)
WHERE game_mod_id IS NOT NULL;

-- Make new columns NOT NULL after data migration
ALTER TABLE images ALTER COLUMN imageable_type SET NOT NULL;
ALTER TABLE images ALTER COLUMN imageable_id SET NOT NULL;
ALTER TABLE images ALTER COLUMN storage_key SET NOT NULL;

-- Drop the old foreign key constraint
ALTER TABLE images DROP CONSTRAINT fk_images_game_mod;

-- Drop the old columns
ALTER TABLE images DROP COLUMN game_mod_id;
ALTER TABLE images DROP COLUMN name;
ALTER TABLE images DROP COLUMN ext;

-- Add new indexes for performance
CREATE INDEX idx_images_imageable_type ON images (imageable_type);
CREATE INDEX idx_images_imageable_id ON images (imageable_id);
CREATE INDEX idx_images_polymorphic ON images (imageable_type, imageable_id);

-- Add constraint for imageable types
ALTER TABLE images ADD CONSTRAINT chk_imageable_type CHECK (imageable_type IN ('MOD', 'MODPACK'));

-- Add new unique constraint to ensure only one header image per imageable entity
CREATE UNIQUE INDEX idx_unique_header_image_polymorphic ON images (imageable_type, imageable_id) WHERE image_type = 'HEADER';

-- Add triggers to validate polymorphic references
CREATE OR REPLACE FUNCTION validate_imageable_reference()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.imageable_type = 'MOD' THEN
        -- Check if imageable_id exists in game_mods table
        IF NOT EXISTS (SELECT 1 FROM game_mods WHERE id = NEW.imageable_id) THEN
            RAISE EXCEPTION 'Invalid imageable_id: % does not exist in game_mods table', NEW.imageable_id;
        END IF;
    ELSIF NEW.imageable_type = 'MODPACK' THEN
        -- Future validation for modpack table will be added when modpacks are implemented
        -- For now, we'll allow it but this can be extended later
        NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER images_imageable_validation
BEFORE INSERT OR UPDATE ON images
FOR EACH ROW EXECUTE FUNCTION validate_imageable_reference(); 