package com.campregheer.fifa_bro_stats.dto.response;

import java.time.Instant;

public record TeamResponse(
        Long id,
        String name,
        Instant createdAt
) {
}