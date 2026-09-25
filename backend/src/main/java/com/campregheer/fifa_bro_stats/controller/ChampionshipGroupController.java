package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipStandingResponse;
import com.campregheer.fifa_bro_stats.service.ChampionshipStandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/championship-groups")
@RequiredArgsConstructor
public class ChampionshipGroupController {

    private final ChampionshipStandingService standingService;

    @GetMapping("/{groupId}/standings")
    public List<ChampionshipStandingResponse> getStandings(
            @PathVariable Long groupId
    ) {
        return standingService.getStandings(groupId);
    }
}