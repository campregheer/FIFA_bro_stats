package com.campregheer.fifa_bro_stats.dto.response;

public record HeadToHeadResponse(
        PlayerStatsResponse player1Stats,
        PlayerStatsResponse player2Stats,
        int totalMatches
) {
}