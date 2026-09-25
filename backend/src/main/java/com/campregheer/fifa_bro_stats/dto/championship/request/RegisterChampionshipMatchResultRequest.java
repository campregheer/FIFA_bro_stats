package com.campregheer.fifa_bro_stats.dto.championship.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegisterChampionshipMatchResultRequest(

        @NotNull
        @Min(0)
        Integer scoreA,

        @NotNull
        @Min(0)
        Integer scoreB,

        @Min(0)
        Integer penaltyScoreA,

        @Min(0)
        Integer penaltyScoreB

) {}
