package com.campregheer.fifa_bro_stats.dto.response;

public record TeamStatsResponse(
        Long teamId,
        String teamName,
        int matchesPlayed,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        double winRate
) {
}