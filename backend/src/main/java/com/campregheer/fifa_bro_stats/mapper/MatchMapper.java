package com.campregheer.fifa_bro_stats.mapper;

import com.campregheer.fifa_bro_stats.dto.response.MatchResponse;
import com.campregheer.fifa_bro_stats.entity.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponse toResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getDate(),
                match.getHomePlayer().getId(),
                match.getHomePlayer().getName(),
                match.getAwayPlayer().getId(),
                match.getAwayPlayer().getName(),
                match.getHomeTeam() != null ? match.getHomeTeam().getId() : null,
                match.getHomeTeamName(),
                match.getAwayTeam() != null ? match.getAwayTeam().getId() : null,
                match.getAwayTeamName(),
                match.getHomeScore(),
                match.getAwayScore(),
                match.getDuration(),
                match.getGameMode(),
                match.getNotes(),
                match.getCreatedAt()
        );
    }
}