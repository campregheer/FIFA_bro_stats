package com.campregheer.fifa_bro_stats.mapper;

import com.campregheer.fifa_bro_stats.dto.request.TeamRequest;
import com.campregheer.fifa_bro_stats.dto.response.TeamResponse;
import com.campregheer.fifa_bro_stats.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(TeamRequest request) {
        return Team.builder()
                .name(request.name())
                .build();
    }

    public TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getCreatedAt()
        );
    }

    public void updateEntity(Team team, TeamRequest request) {
        team.setName(request.name());
    }
}