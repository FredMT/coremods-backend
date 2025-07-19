-- Update the constraint on commentable_type to include 'bug_report'
ALTER TABLE comments DROP CONSTRAINT chk_commentable_type;
ALTER TABLE comments ADD CONSTRAINT chk_commentable_type CHECK (commentable_type IN ('mod', 'image', 'collection', 'bug_report'));

-- Drop the existing parent_id foreign key constraint
ALTER TABLE comments DROP CONSTRAINT fk_comments_parent;

-- Add a new column to track parent type
ALTER TABLE comments ADD COLUMN parent_type VARCHAR(10) DEFAULT 'comment';

-- Add constraint to ensure parent_type is valid
ALTER TABLE comments ADD CONSTRAINT chk_parent_type CHECK (parent_type IN ('comment', 'bug_report'));

-- Add triggers to validate parent_id references based on parent_type
CREATE OR REPLACE FUNCTION validate_parent_reference()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.parent_id IS NOT NULL THEN
        IF NEW.parent_type = 'comment' THEN
            -- Check if parent_id exists in comments table
            IF NOT EXISTS (SELECT 1 FROM comments WHERE id = NEW.parent_id) THEN
                RAISE EXCEPTION 'Invalid parent_id: % does not exist in comments table', NEW.parent_id;
            END IF;
        ELSIF NEW.parent_type = 'bug_report' THEN
            -- Check if parent_id exists in bug_reports table
            IF NOT EXISTS (SELECT 1 FROM bug_reports WHERE id = NEW.parent_id) THEN
                RAISE EXCEPTION 'Invalid parent_id: % does not exist in bug_reports table', NEW.parent_id;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER comments_parent_validation
BEFORE INSERT OR UPDATE ON comments
FOR EACH ROW EXECUTE FUNCTION validate_parent_reference(); 