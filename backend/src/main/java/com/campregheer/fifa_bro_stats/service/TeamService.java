package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.request.TeamRequest;
import com.campregheer.fifa_bro_stats.dto.response.TeamResponse;
import com.campregheer.fifa_bro_stats.entity.Team;
import com.campregheer.fifa_bro_stats.exception.ResourceNotFoundException;
import com.campregheer.fifa_bro_stats.mapper.TeamMapper;
import com.campregheer.fifa_bro_stats.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Transactional
    public TeamResponse create(TeamRequest request) {
        Team team = teamMapper.toEntity(request);
        Team saved = teamRepository.save(team);
        return teamMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findAll() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        return teamMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        Team team = findEntityById(id);
        teamMapper.updateEntity(team, request);
        return teamMapper.toResponse(team);
    }

    @Transactional
    public void delete(Long id) {
        teamRepository.delete(findEntityById(id));
    }

    Team findEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado com id: " + id));
    }
}