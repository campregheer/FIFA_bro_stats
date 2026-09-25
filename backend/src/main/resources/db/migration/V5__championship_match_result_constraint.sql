UPDATE championship_matches
SET played = false
WHERE played = true
  AND (score_a IS NULL OR score_b IS NULL);

ALTER TABLE championship_matches
    ADD CONSTRAINT chk_championship_match_result
        CHECK (
            played = FALSE
                OR (
                score_a IS NOT NULL
                    AND score_b IS NOT NULL
                )
            );