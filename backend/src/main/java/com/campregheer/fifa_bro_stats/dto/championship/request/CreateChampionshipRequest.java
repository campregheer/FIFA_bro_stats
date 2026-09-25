package com.campregheer.fifa_bro_stats.dto.championship.request;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateChampionshipRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        ChampionshipMatchFormat matchFormat

) {}