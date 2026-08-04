package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.response.HeadToHeadResponse;
import com.campregheer.fifa_bro_stats.dto.response.PlayerStatsResponse;
import com.campregheer.fifa_bro_stats.dto.response.TeamStatsResponse;
import com.campregheer.fifa_bro_stats.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/general")
    public ResponseEntity<List<PlayerStatsResponse>> general() {
        return ResponseEntity.ok(statisticsService.getGeneralRanking());
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<PlayerStatsResponse> playerStats(@PathVariable Long id) {
        return ResponseEntity.ok(statisticsService.getPlayerStats(id));
    }

    @GetMapping("/team/{id}")
    public ResponseEntity<TeamStatsResponse> teamStats(@PathVariable Long id) {
        return ResponseEntity.ok(statisticsService.getTeamStats(id));
    }

    @GetMapping("/head-to-head")
    public ResponseEntity<HeadToHeadResponse> headToHead(
            @RequestParam Long player1Id,
            @RequestParam Long player2Id) {
        return ResponseEntity.ok(statisticsService.getHeadToHead(player1Id, player2Id));
    }
}