-- Remove unused columns from game_mod_categories table
DO $$
BEGIN
    -- Check if is_preset column exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'game_mod_categories' AND column_name = 'is_preset'
    ) THEN
        ALTER TABLE game_mod_categories DROP COLUMN is_preset;
    END IF;
    
    -- Check if display_order column exists
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'game_mod_categories' AND column_name = 'display_order'
    ) THEN
        ALTER TABLE game_mod_categories DROP COLUMN display_order;
    END IF;
END
$$; 