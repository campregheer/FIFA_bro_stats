package com.campregheer.fifa_bro_stats.dto.response;

public record PlayerStatsResponse(
        Long playerId,
        String playerName,
        int matchesPlayed,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst,
        int goalDifference,
        double winRate,      // aproveitamento em %
        int points,          // vitória=3, empate=1, derrota=0
        String currentStreak // ex: "3V", "2D", "1E"
) {
}