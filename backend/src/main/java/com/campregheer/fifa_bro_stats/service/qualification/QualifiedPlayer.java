package com.campregheer.fifa_bro_stats.service.qualification;

import com.campregheer.fifa_bro_stats.entity.Player;

public record QualifiedPlayer(
        Player player,
        Long groupId,
        String groupName,
        Integer groupPosition,
        Integer points,
        Integer goalDifference,
        Integer goalsFor
) {
}
