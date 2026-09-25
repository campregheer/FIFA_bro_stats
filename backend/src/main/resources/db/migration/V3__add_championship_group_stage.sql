-- =============================================
-- GROUPS
-- =============================================

CREATE TABLE championship_groups (
                                     id BIGSERIAL PRIMARY KEY,
                                     championship_id BIGINT NOT NULL
                                         REFERENCES championships(id)
                                             ON DELETE CASCADE,

                                     name VARCHAR(10) NOT NULL,

                                     UNIQUE (championship_id, name)
);


CREATE TABLE championship_group_participants (
                                                 group_id BIGINT NOT NULL
                                                     REFERENCES championship_groups(id)
                                                         ON DELETE CASCADE,

                                                 player_id BIGINT NOT NULL
                                                     REFERENCES players(id),

                                                 PRIMARY KEY (group_id, player_id)
);


-- =============================================
-- UPDATE CHAMPIONSHIP MATCHES
-- =============================================

ALTER TABLE championship_matches
    ADD COLUMN championship_id BIGINT
        REFERENCES championships(id)
            ON DELETE CASCADE;

ALTER TABLE championship_matches
    ADD COLUMN stage VARCHAR(30);

ALTER TABLE championship_matches
    ADD COLUMN group_id BIGINT
        REFERENCES championship_groups(id)
            ON DELETE CASCADE;

ALTER TABLE championship_matches
    ADD COLUMN player_a_id BIGINT
        REFERENCES players(id);

ALTER TABLE championship_matches
    ADD COLUMN player_b_id BIGINT
        REFERENCES players(id);

ALTER TABLE championship_matches
    ADD COLUMN played BOOLEAN NOT NULL DEFAULT FALSE;


-- matchup deixa de ser obrigatório
ALTER TABLE championship_matches
    ALTER COLUMN matchup_id DROP NOT NULL;

-- leg_number só é necessário no mata-mata
ALTER TABLE championship_matches
    ALTER COLUMN leg_number DROP NOT NULL;

-- placar só existe depois que a partida for jogada
ALTER TABLE championship_matches
    ALTER COLUMN score_a DROP NOT NULL;

ALTER TABLE championship_matches
    ALTER COLUMN score_b DROP NOT NULL;


-- =============================================
-- INDEXES
-- =============================================

CREATE INDEX idx_championship_groups_championship
    ON championship_groups(championship_id);

CREATE INDEX idx_championship_group_participants_group
    ON championship_group_participants(group_id);

CREATE INDEX idx_championship_matches_championship
    ON championship_matches(championship_id);

CREATE INDEX idx_championship_matches_group
    ON championship_matches(group_id);

CREATE INDEX idx_championship_matches_stage
    ON championship_matches(stage);