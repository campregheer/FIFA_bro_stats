package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.response.DashboardResponse;
import com.campregheer.fifa_bro_stats.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(statisticsService.getDashboard());
    }
}