package com.campregheer.fifa_bro_stats.controller;

import com.campregheer.fifa_bro_stats.dto.request.PlayerRequest;
import com.campregheer.fifa_bro_stats.dto.response.PlayerResponse;
import com.campregheer.fifa_bro_stats.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse response = playerService.create(request);
        return ResponseEntity.created(URI.create("/players/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> findAll() {
        return ResponseEntity.ok(playerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> update(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(playerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}