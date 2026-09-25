package com.campregheer.fifa_bro_stats.dto.championship.response;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchupStatus;
import java.util.List;

public record ChampionshipMatchupResponse(Long id, Integer position,
        ChampionshipParticipantResponse playerA, ChampionshipParticipantResponse playerB,
        ChampionshipParticipantResponse winner, ChampionshipMatchupStatus status,
        List<ChampionshipMatchResponse> matches) {}
