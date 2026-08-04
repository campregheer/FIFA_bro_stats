package com.campregheer.fifa_bro_stats.mapper;

import com.campregheer.fifa_bro_stats.dto.request.PlayerRequest;
import com.campregheer.fifa_bro_stats.dto.response.PlayerResponse;
import com.campregheer.fifa_bro_stats.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        return Player.builder()
                .name(request.name())
                .nickname(request.nickname())
                .build();
    }

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getNickname(),
                player.getCreatedAt()
        );
    }

    public void updateEntity(Player player, PlayerRequest request) {
            player.setName(request.name());
            player.setNickname(request.nickname());

    }
}