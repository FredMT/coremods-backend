CREATE TABLE mod_distribution_settings (
    id BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL UNIQUE,

    use_custom_permissions BOOLEAN NOT NULL,
    custom_permission_instructions TEXT,

    has_restricted_assets_from_others BOOLEAN,
    upload_to_other_sites VARCHAR(50),
    convert_to_other_games VARCHAR(50),
    modify_and_reupload VARCHAR(50),
    use_assets_in_own_files VARCHAR(50),
    restrict_commercial_use BOOLEAN,

    credits TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Validate ENUM values if present (nullable if using custom instructions)
    CONSTRAINT check_upload_to_other_sites CHECK (
        upload_to_other_sites IS NULL OR upload_to_other_sites IN ('YES_CREDIT', 'NO')
    ),
    CONSTRAINT check_convert_to_other_games CHECK (
        convert_to_other_games IS NULL OR convert_to_other_games IN ('YES_CREDIT', 'NO')
    ),
    CONSTRAINT check_modify_and_reupload CHECK (
        modify_and_reupload IS NULL OR modify_and_reupload IN ('YES_NO_CREDIT', 'YES_CREDIT', 'NOT_WITHOUT_PERMISSION', 'ABSOLUTELY_NOT')
    ),
    CONSTRAINT check_use_assets_in_own_files CHECK (
        use_assets_in_own_files IS NULL OR use_assets_in_own_files IN ('YES_NO_CREDIT', 'YES_CREDIT', 'NOT_WITHOUT_PERMISSION', 'ABSOLUTELY_NOT')
    ),

    -- Custom permission mode logic: enforce exclusive paths
    CONSTRAINT check_custom_permissions_logic CHECK (
        (
            use_custom_permissions = true AND
            custom_permission_instructions IS NOT NULL AND
            LENGTH(TRIM(custom_permission_instructions)) > 0 AND
            has_restricted_assets_from_others IS NULL AND
            upload_to_other_sites IS NULL AND
            convert_to_other_games IS NULL AND
            modify_and_reupload IS NULL AND
            use_assets_in_own_files IS NULL AND
            restrict_commercial_use IS NULL
        ) OR (
            use_custom_permissions = false AND
            custom_permission_instructions IS NULL AND
            has_restricted_assets_from_others IS NOT NULL AND
            upload_to_other_sites IS NOT NULL AND
            convert_to_other_games IS NOT NULL AND
            modify_and_reupload IS NOT NULL AND
            use_assets_in_own_files IS NOT NULL AND
            restrict_commercial_use IS NOT NULL
        )
    ),

    CONSTRAINT fk_mod_distribution_settings_mod FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

CREATE INDEX idx_mod_distribution_settings_mod_id ON mod_distribution_settings (mod_id);
