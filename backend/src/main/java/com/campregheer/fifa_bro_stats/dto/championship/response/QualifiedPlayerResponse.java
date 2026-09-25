package com.campregheer.fifa_bro_stats.dto.championship.response;

public record QualifiedPlayerResponse(
        Long playerId,
        String playerName,
        String group,
        Integer groupPosition,
        Integer points,
        Integer goalDifference,
        Integer goalsFor
) {
}
