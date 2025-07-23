CREATE TABLE mod_required_dlcs
(
    id     BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL,
    dlc_id BIGINT NOT NULL,

    CONSTRAINT fk_mod_required_dlcs_mod_id
        FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,

    CONSTRAINT unique_mod_dlc UNIQUE (mod_id, dlc_id)
);

CREATE TABLE mod_required_mods
(
    id                BIGSERIAL PRIMARY KEY,
    mod_id            BIGINT NOT NULL,
    required_mod_id   BIGINT NOT NULL,
    requirement_notes TEXT,

    CONSTRAINT fk_mod_required_mods_mod_id
        FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,
    CONSTRAINT fk_mod_required_mods_required_mod_id
        FOREIGN KEY (required_mod_id) REFERENCES game_mods (id) ON DELETE CASCADE,

    CONSTRAINT unique_mod_required_mod UNIQUE (mod_id, required_mod_id)
);

CREATE TABLE mod_external_requirements
(
    id     BIGSERIAL PRIMARY KEY,
    mod_id BIGINT NOT NULL,
    name   TEXT   NOT NULL,
    url    TEXT   NOT NULL,
    notes  TEXT,

    CONSTRAINT fk_mod_external_requirements_mod_id
        FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

CREATE TABLE mod_mirrors
(
    id          BIGSERIAL PRIMARY KEY,
    mod_id      BIGINT NOT NULL,
    mirror_name TEXT   NOT NULL,
    mirror_url  TEXT   NOT NULL,

    CONSTRAINT fk_mod_mirrors_mod_id
        FOREIGN KEY (mod_id) REFERENCES game_mods (id) ON DELETE CASCADE
);

CREATE INDEX idx_mod_required_dlcs_mod_id ON mod_required_dlcs (mod_id);
CREATE INDEX idx_mod_required_dlcs_dlc_id ON mod_required_dlcs (dlc_id);
CREATE INDEX idx_mod_required_mods_mod_id ON mod_required_mods (mod_id);
CREATE INDEX idx_mod_required_mods_required_mod_id ON mod_required_mods (required_mod_id);
CREATE INDEX idx_mod_external_requirements_mod_id ON mod_external_requirements (mod_id);
CREATE INDEX idx_mod_mirrors_mod_id ON mod_mirrors (mod_id);
