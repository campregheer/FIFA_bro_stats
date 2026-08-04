package com.campregheer.fifa_bro_stats.dto.response;

import java.time.Instant;

public record PlayerResponse(
        Long id,
        String name,
        String nickname,
        Instant createdAt
) {
}