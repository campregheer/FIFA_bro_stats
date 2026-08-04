package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.request.MatchRequest;
import com.campregheer.fifa_bro_stats.dto.response.MatchResponse;
import com.campregheer.fifa_bro_stats.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        MatchResponse response = matchService.create(request);
        return ResponseEntity.created(URI.create("/matches/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> findAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> update(@PathVariable Long id, @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}