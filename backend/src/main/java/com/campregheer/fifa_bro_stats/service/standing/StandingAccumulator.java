package com.campregheer.fifa_bro_stats.service.standing;

import com.campregheer.fifa_bro_stats.entity.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StandingAccumulator {

    private final Player player;

    private int points;
    private int played;
    private int wins;
    private int draws;
    private int losses;

    private int goalsFor;
    private int goalsAgainst;

    public void registerMatch(
            int goalsScored,
            int goalsConceded
    ) {

        played++;

        goalsFor += goalsScored;
        goalsAgainst += goalsConceded;

        if (goalsScored > goalsConceded) {
            wins++;
            points += 3;

        } else if (goalsScored == goalsConceded) {
            draws++;
            points++;

        } else {
            losses++;
        }
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
}