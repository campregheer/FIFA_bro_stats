package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.championship.request.CreateChampionshipRequest;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipDetailsResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.QualifiedPlayerResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipRoundResponse;
import com.campregheer.fifa_bro_stats.service.ChampionshipService;
import com.campregheer.fifa_bro_stats.service.ChampionshipQualificationService;
import com.campregheer.fifa_bro_stats.service.ChampionshipKnockoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/championships")
@RequiredArgsConstructor
public class ChampionshipController {

    private final ChampionshipService championshipService;
    private final ChampionshipQualificationService qualificationService;
    private final ChampionshipKnockoutService knockoutService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChampionshipResponse create(
            @Valid @RequestBody CreateChampionshipRequest request
    ) {
        return championshipService.create(request);
    }

    @PutMapping("/{id}")
    public ChampionshipResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateChampionshipRequest request
    ) {
        return championshipService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        championshipService.delete(id);
    }

    @GetMapping
    public List<ChampionshipResponse> findAll() {
        return championshipService.findAll();
    }

    @GetMapping("/{id}")
    public ChampionshipResponse findById(
            @PathVariable Long id
    ) {
        return championshipService.findById(id);
    }

    @GetMapping("/{id}/details")
    public ChampionshipDetailsResponse findDetails(@PathVariable Long id) {
        return championshipService.findDetailsById(id);
    }

    @PostMapping("/{championshipId}/participants/{playerId}")
    public ChampionshipResponse addParticipant(
            @PathVariable Long championshipId,
            @PathVariable Long playerId
    ) {
        return championshipService.addParticipant(
                championshipId,
                playerId
        );
    }

    @DeleteMapping("/{championshipId}/participants/{playerId}")
    public ChampionshipResponse removeParticipant(
            @PathVariable Long championshipId,
            @PathVariable Long playerId
    ) {
        return championshipService.removeParticipant(
                championshipId,
                playerId
        );
    }

    @PostMapping("/{id}/start")
    public ChampionshipDetailsResponse start(
            @PathVariable Long id
    ) {
        return championshipService.startChampionship(id);
    }

    @GetMapping("/{id}/qualified")
    public List<QualifiedPlayerResponse> getQualified(@PathVariable Long id) {
        return qualificationService.getQualifiedPlayers(id)
                .stream()
                .map(qualified -> new QualifiedPlayerResponse(
                        qualified.player().getId(),
                        qualified.player().getName(),
                        qualified.groupName(),
                        qualified.groupPosition(),
                        qualified.points(),
                        qualified.goalDifference(),
                        qualified.goalsFor()
                ))
                .toList();
    }

    @PostMapping("/{id}/knockout")
    public ChampionshipRoundResponse generateKnockout(@PathVariable Long id) {
        return knockoutService.generateFirstRound(id);
    }
}
