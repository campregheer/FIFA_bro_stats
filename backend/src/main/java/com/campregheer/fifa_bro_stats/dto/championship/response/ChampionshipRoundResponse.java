package com.campregheer.fifa_bro_stats.dto.championship.response;

import java.util.List;

public record ChampionshipRoundResponse(Long id, String name, Integer orderIndex,
        List<ChampionshipMatchupResponse> matchups) {}
