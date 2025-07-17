-- Create bug_reports table
CREATE TABLE bug_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    mod_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW_ISSUE',
    priority VARCHAR(20) NOT NULL DEFAULT 'NOT_SET',
    bug_status_open BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_bug_reports_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bug_reports_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id)
);

-- Create indexes for performance
CREATE INDEX idx_bug_reports_user_id ON bug_reports (user_id);
CREATE INDEX idx_bug_reports_mod_id ON bug_reports (mod_id);
CREATE INDEX idx_bug_reports_status ON bug_reports (status);
CREATE INDEX idx_bug_reports_priority ON bug_reports (priority);
CREATE INDEX idx_bug_reports_bug_status_open ON bug_reports (bug_status_open);
CREATE INDEX idx_bug_reports_created_at ON bug_reports (created_at DESC);
CREATE INDEX idx_bug_reports_updated_at ON bug_reports (updated_at DESC);

-- Add constraints to ensure enum values are valid
ALTER TABLE bug_reports ADD CONSTRAINT chk_bug_report_status 
    CHECK (status IN ('NEW_ISSUE', 'BEING_LOOKED_AT', 'FIXED', 'KNOWN_ISSUES', 'DUPLICATES', 'NOT_A_BUG', 'WONT_FIX', 'NEED_MORE_INFO'));

ALTER TABLE bug_reports ADD CONSTRAINT chk_bug_report_priority 
    CHECK (priority IN ('NOT_SET', 'LOW', 'MEDIUM', 'HIGH'));

-- Add constraint to ensure description doesn't exceed 5000 characters
ALTER TABLE bug_reports ADD CONSTRAINT chk_bug_report_description_length 
    CHECK (LENGTH(description) <= 5000); 