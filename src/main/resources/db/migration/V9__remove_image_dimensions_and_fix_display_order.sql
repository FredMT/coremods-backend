-- Remove width and height columns from images table
ALTER TABLE images DROP COLUMN IF EXISTS width;
ALTER TABLE images DROP COLUMN IF EXISTS height;

-- Set display_order to NULL for all header images since they don't need ordering
UPDATE images SET display_order = NULL WHERE image_type = 'HEADER'; 