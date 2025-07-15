-- Add columns to support hierarchical structure
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'game_mod_categories' AND column_name = 'parent_id'
    ) THEN
        ALTER TABLE game_mod_categories ADD COLUMN parent_id BIGINT;
        
        -- Add foreign key constraint for parent-child relationship
        ALTER TABLE game_mod_categories 
        ADD CONSTRAINT fk_game_mod_category_parent 
        FOREIGN KEY (parent_id) REFERENCES game_mod_categories (id) ON DELETE CASCADE;
    END IF;
END
$$;

-- Drop the old unique constraint if it exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'uk_game_category' AND conrelid = 'game_mod_categories'::regclass
    ) THEN
        ALTER TABLE game_mod_categories DROP CONSTRAINT uk_game_category;
    END IF;
END
$$;

-- Add new unique constraint that accounts for hierarchical structure
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'uk_game_category_hierarchy' AND conrelid = 'game_mod_categories'::regclass
    ) THEN
        ALTER TABLE game_mod_categories 
        ADD CONSTRAINT uk_game_category_hierarchy 
        UNIQUE (game_id, category_name, parent_id);
    END IF;
END
$$;

-- Create index for better performance on parent_id queries if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE indexname = 'idx_game_mod_categories_parent_id'
    ) THEN
        CREATE INDEX idx_game_mod_categories_parent_id ON game_mod_categories (parent_id);
    END IF;
END
$$; 