package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.request.PlayerRequest;
import com.campregheer.fifa_bro_stats.dto.response.PlayerResponse;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.exception.ResourceNotFoundException;
import com.campregheer.fifa_bro_stats.mapper.PlayerMapper;
import com.campregheer.fifa_bro_stats.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Transactional
    public PlayerResponse create(PlayerRequest request) {
        Player player = playerMapper.toEntity(request);
        Player saved = playerRepository.save(player);
        return playerMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> findAll() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(Long id) {
        Player player = findEntityById(id);
        return playerMapper.toResponse(player);
    }

    @Transactional
    public PlayerResponse update(Long id, PlayerRequest request) {
        Player player = findEntityById(id);
        playerMapper.updateEntity(player, request);
        return playerMapper.toResponse(player);
    }

    @Transactional
    public void delete(Long id) {
        Player player = findEntityById(id);
        playerRepository.delete(player);
    }

    private Player findEntityById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado com id: " + id));
    }
}