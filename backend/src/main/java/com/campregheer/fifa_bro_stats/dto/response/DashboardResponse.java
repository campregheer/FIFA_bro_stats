package com.campregheer.fifa_bro_stats.dto.response;

import java.util.List;

public record DashboardResponse(
        long totalPlayers,
        long totalTeams,
        long totalMatches,
        List<PlayerStatsResponse> topRanking,
        List<MatchResponse> recentMatches
) {
}