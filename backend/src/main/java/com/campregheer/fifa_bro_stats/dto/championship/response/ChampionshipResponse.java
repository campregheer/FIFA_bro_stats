package com.campregheer.fifa_bro_stats.dto.championship.response;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchFormat;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStatus;

import java.time.Instant;
import java.util.Set;

public record ChampionshipResponse(

        Long id,
        String name,
        ChampionshipStatus status,
        ChampionshipMatchFormat matchFormat,
        Set<ChampionshipParticipantResponse> participants,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt

) {}