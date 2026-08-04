package com.campregheer.fifa_bro_stats.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record MatchRequest(

        @NotNull(message = "A data é obrigatória")
        LocalDateTime date,

        @NotNull(message = "O jogador da casa é obrigatório")
        Long homePlayerId,

        @NotNull(message = "O jogador visitante é obrigatório")
        Long awayPlayerId,

        // Opcional: se informado, o nome do time vem do catálogo (Team)
        Long homeTeamId,
        Long awayTeamId,

        // Obrigatório apenas se o *TeamId correspondente for nulo (validado no service)
        @Size(max = 100)
        String homeTeamName,

        @Size(max = 100)
        String awayTeamName,

        @NotNull(message = "O placar da casa é obrigatório")
        @Min(value = 0, message = "O placar não pode ser negativo")
        Integer homeScore,

        @NotNull(message = "O placar visitante é obrigatório")
        @Min(value = 0, message = "O placar não pode ser negativo")
        Integer awayScore,

        @Min(value = 0, message = "A duração não pode ser negativa")
        Integer duration,

        @Size(max = 50)
        String gameMode,

        @Size(max = 1000)
        String notes
) {
}