ALTER TABLE users
    DROP COLUMN first_name;

ALTER TABLE users
    DROP COLUMN last_name;

ALTER TABLE users
    ALTER COLUMN username TYPE VARCHAR(12) USING (username::VARCHAR(12));