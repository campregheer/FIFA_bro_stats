package com.campregheer.fifa_bro_stats.dto.championship.response;

public record ChampionshipStandingResponse(

        Integer position,

        ChampionshipParticipantResponse player,

        Integer points,
        Integer played,
        Integer wins,
        Integer draws,
        Integer losses,

        Integer goalsFor,
        Integer goalsAgainst,
        Integer goalDifference

) {}