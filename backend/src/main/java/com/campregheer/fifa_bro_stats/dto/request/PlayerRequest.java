package com.campregheer.fifa_bro_stats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @Size(max = 50, message = "O apelido deve ter no máximo 50 caracteres")
        String nickname
) {
}
