package com.campregheer.fifa_bro_stats.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        LocalDateTime date,
        Long homePlayerId,
        String homePlayerName,
        Long awayPlayerId,
        String awayPlayerName,
        Long homeTeamId,
        String homeTeamName,
        Long awayTeamId,
        String awayTeamName,
        Integer homeScore,
        Integer awayScore,
        Integer duration,
        String gameMode,
        String notes,
        Instant createdAt
) {
}