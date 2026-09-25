package com.campregheer.fifa_bro_stats.dto.championship.response;

import com.campregheer.fifa_bro_stats.entity.ChampionshipStage;

import java.time.LocalDateTime;

public record ChampionshipMatchResponse(
        Long id,
        ChampionshipStage stage,
        ChampionshipParticipantResponse playerA,
        ChampionshipParticipantResponse playerB,
        Integer scoreA,
        Integer scoreB,
        Boolean played,
        LocalDateTime date
) {}