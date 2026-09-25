CREATE TABLE championships (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(100) NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                               match_format VARCHAR(30) NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT now(),
                               started_at TIMESTAMP,
                               finished_at TIMESTAMP
);

CREATE TABLE championship_participants (
                                           championship_id BIGINT NOT NULL REFERENCES championships(id) ON DELETE CASCADE,
                                           player_id BIGINT NOT NULL REFERENCES players(id),
                                           PRIMARY KEY (championship_id, player_id)
);

CREATE TABLE championship_rounds (
                                     id BIGSERIAL PRIMARY KEY,
                                     championship_id BIGINT NOT NULL REFERENCES championships(id) ON DELETE CASCADE,
                                     name VARCHAR(50) NOT NULL,
                                     order_index INTEGER NOT NULL,
                                     UNIQUE (championship_id, order_index)
);

CREATE TABLE championship_matchups (
                                       id BIGSERIAL PRIMARY KEY,
                                       round_id BIGINT NOT NULL REFERENCES championship_rounds(id) ON DELETE CASCADE,
                                       position INTEGER NOT NULL,
                                       player_a_id BIGINT REFERENCES players(id),
                                       player_b_id BIGINT REFERENCES players(id),
                                       winner_id BIGINT REFERENCES players(id),
                                       penalty_score_a INTEGER CHECK (penalty_score_a >= 0),
                                       penalty_score_b INTEGER CHECK (penalty_score_b >= 0),
                                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                       UNIQUE (round_id, position)
);

CREATE TABLE championship_matches (
                                      id BIGSERIAL PRIMARY KEY,
                                      matchup_id BIGINT NOT NULL REFERENCES championship_matchups(id) ON DELETE CASCADE,
                                      leg_number INTEGER NOT NULL CHECK (leg_number >= 1),
                                      date TIMESTAMP NOT NULL,
                                      score_a INTEGER NOT NULL CHECK (score_a >= 0),
                                      score_b INTEGER NOT NULL CHECK (score_b >= 0),
                                      created_at TIMESTAMP NOT NULL DEFAULT now(),
                                      UNIQUE (matchup_id, leg_number)
);

CREATE INDEX idx_championship_rounds_championship ON championship_rounds(championship_id);
CREATE INDEX idx_championship_matchups_round ON championship_matchups(round_id);
CREATE INDEX idx_championship_matches_matchup ON championship_matches(matchup_id);