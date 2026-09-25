package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.championship.request.RegisterChampionshipMatchResultRequest;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipMatchResponse;
import com.campregheer.fifa_bro_stats.service.ChampionshipMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/championship-matches")
@RequiredArgsConstructor
public class ChampionshipMatchController {

    private final ChampionshipMatchService championshipMatchService;

    @PutMapping("/{matchId}/result")
    public ChampionshipMatchResponse registerResult(
            @PathVariable Long matchId,
            @Valid @RequestBody RegisterChampionshipMatchResultRequest request
    ) {
        return championshipMatchService.registerResult(
                matchId,
                request
        );
    }
}