CREATE TABLE mod_distribution_settings_permissions_log (
    id BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL,
    changed_fields JSONB NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_mod_distribution_settings_permissions_log_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

CREATE INDEX idx_mod_distribution_settings_permissions_log_mod_id ON mod_distribution_settings_permissions_log (mod_id);
CREATE INDEX idx_mod_distribution_settings_permissions_log_changed_at ON mod_distribution_settings_permissions_log (changed_at); 