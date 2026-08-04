package com.campregheer.fifa_bro_stats.statistics;

import com.campregheer.fifa_bro_stats.dto.response.PlayerStatsResponse;
import com.campregheer.fifa_bro_stats.entity.Match;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class PlayerStatsCalculator {

    public PlayerStatsResponse calculate(Long playerId, String playerName, List<Match> matches) {
        List<Match> sorted = matches.stream()
                .sorted(Comparator.comparing(Match::getDate))
                .toList();

        int wins = 0, draws = 0, losses = 0, goalsFor = 0, goalsAgainst = 0;
        List<MatchResult> chronologicalResults = new ArrayList<>();

        for (Match match : sorted) {
            boolean isHome = match.getHomePlayer().getId().equals(playerId);
            int ownScore = isHome ? match.getHomeScore() : match.getAwayScore();
            int opponentScore = isHome ? match.getAwayScore() : match.getHomeScore();

            goalsFor += ownScore;
            goalsAgainst += opponentScore;

            MatchResult result;
            if (ownScore > opponentScore) {
                wins++;
                result = MatchResult.WIN;
            } else if (ownScore == opponentScore) {
                draws++;
                result = MatchResult.DRAW;
            } else {
                losses++;
                result = MatchResult.LOSS;
            }
            chronologicalResults.add(result);
        }

        int matchesPlayed = sorted.size();
        int goalDifference = goalsFor - goalsAgainst;
        int points = wins * 3 + draws;
        double winRate = matchesPlayed == 0 ? 0.0 : (wins * 100.0) / matchesPlayed;
        String currentStreak = calculateCurrentStreak(chronologicalResults);

        return new PlayerStatsResponse(
                playerId,
                playerName,
                matchesPlayed,
                wins,
                draws,
                losses,
                goalsFor,
                goalsAgainst,
                goalDifference,
                Math.round(winRate * 100.0) / 100.0,
                points,
                currentStreak
        );
    }

    private String calculateCurrentStreak(List<MatchResult> chronologicalResults) {
        if (chronologicalResults.isEmpty()) {
            return "-";
        }

        List<MatchResult> reversed = new ArrayList<>(chronologicalResults);
        Collections.reverse(reversed);

        MatchResult last = reversed.get(0);
        int count = 0;
        for (MatchResult result : reversed) {
            if (result != last) break;
            count++;
        }

        String label = switch (last) {
            case WIN -> "V";
            case DRAW -> "E";
            case LOSS -> "D";
        };
        return count + label;
    }
}