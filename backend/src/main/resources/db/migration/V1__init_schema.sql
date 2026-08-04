CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     nickname VARCHAR(50),
     created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    home_player_id BIGINT NOT NULL REFERENCES players(id),
    away_player_id BIGINT NOT NULL REFERENCES players(id),
    home_team_id BIGINT REFERENCES teams(id),
    away_team_id BIGINT REFERENCES teams(id),
    home_team_name VARCHAR(100) NOT NULL,
    away_team_name VARCHAR(100) NOT NULL,
    home_score INTEGER NOT NULL CHECK (home_score >= 0),
    away_score INTEGER NOT NULL CHECK (away_score >= 0),
    duration INTEGER,
    game_mode VARCHAR(50),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_matches_home_player ON matches(home_player_id);
CREATE INDEX idx_matches_away_player ON matches(away_player_id);
CREATE INDEX idx_matches_date ON matches(date);