package com.campregheer.fifa_bro_stats.dto.championship.response;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchFormat;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStatus;

import java.time.Instant;
import java.util.List;

public record ChampionshipDetailsResponse(
        Long id,
        String name,
        ChampionshipStatus status,
        ChampionshipMatchFormat matchFormat,
        List<ChampionshipGroupResponse> groups,
        List<ChampionshipRoundResponse> rounds,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {}
