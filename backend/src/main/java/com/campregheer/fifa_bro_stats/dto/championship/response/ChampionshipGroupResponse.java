package com.campregheer.fifa_bro_stats.dto.championship.response;

import java.util.List;

public record ChampionshipGroupResponse(
        Long id,
        String name,
        List<ChampionshipParticipantResponse> participants,
        List<ChampionshipMatchResponse> matches
) {}