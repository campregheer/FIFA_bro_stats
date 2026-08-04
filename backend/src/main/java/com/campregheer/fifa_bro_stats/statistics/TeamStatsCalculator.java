package com.campregheer.fifa_bro_stats.statistics;

import com.campregheer.fifa_bro_stats.dto.response.TeamStatsResponse;
import com.campregheer.fifa_bro_stats.entity.Match;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamStatsCalculator {

    public TeamStatsResponse calculate(Long teamId, String teamName, List<Match> matches) {
        int wins = 0, draws = 0, losses = 0, goalsFor = 0, goalsAgainst = 0;

        for (Match match : matches) {
            boolean isHome = match.getHomeTeam() != null && match.getHomeTeam().getId().equals(teamId);
            int ownScore = isHome ? match.getHomeScore() : match.getAwayScore();
            int opponentScore = isHome ? match.getAwayScore() : match.getHomeScore();

            goalsFor += ownScore;
            goalsAgainst += opponentScore;

            if (ownScore > opponentScore) wins++;
            else if (ownScore == opponentScore) draws++;
            else losses++;
        }

        int matchesPlayed = matches.size();
        double winRate = matchesPlayed == 0 ? 0.0 : (wins * 100.0) / matchesPlayed;

        return new TeamStatsResponse(
                teamId,
                teamName,
                matchesPlayed,
                wins,
                draws,
                losses,
                goalsFor,
                goalsAgainst,
                goalsFor - goalsAgainst,
                Math.round(winRate * 100.0) / 100.0
        );
    }
}