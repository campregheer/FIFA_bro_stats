package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.request.MatchRequest;
import com.campregheer.fifa_bro_stats.dto.response.MatchResponse;
import com.campregheer.fifa_bro_stats.entity.Match;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.entity.Team;
import com.campregheer.fifa_bro_stats.exception.ResourceNotFoundException;
import com.campregheer.fifa_bro_stats.mapper.MatchMapper;
import com.campregheer.fifa_bro_stats.repository.MatchRepository;
import com.campregheer.fifa_bro_stats.repository.PlayerRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final TeamService teamService;
    private final MatchMapper matchMapper;

    @Transactional
    public MatchResponse create(MatchRequest request) {
        Player homePlayer = findPlayerById(request.homePlayerId());
        Player awayPlayer = findPlayerById(request.awayPlayerId());

        Team homeTeam = request.homeTeamId() != null ? teamService.findEntityById(request.homeTeamId()) : null;
        Team awayTeam = request.awayTeamId() != null ? teamService.findEntityById(request.awayTeamId()) : null;

        String homeTeamName = resolveTeamName(homeTeam, request.homeTeamName(), "casa");
        String awayTeamName = resolveTeamName(awayTeam, request.awayTeamName(), "visitante");

        Match match = Match.builder()
                .date(request.date())
                .homePlayer(homePlayer)
                .awayPlayer(awayPlayer)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamName(homeTeamName)
                .awayTeamName(awayTeamName)
                .homeScore(request.homeScore())
                .awayScore(request.awayScore())
                .duration(request.duration())
                .gameMode(request.gameMode())
                .notes(request.notes())
                .build();

        Match saved = matchRepository.save(match);
        return matchMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findAll() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse findById(Long id) {
        return matchMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public MatchResponse update(Long id, MatchRequest request) {
        Match match = findEntityById(id);

        Player homePlayer = findPlayerById(request.homePlayerId());
        Player awayPlayer = findPlayerById(request.awayPlayerId());
        Team homeTeam = request.homeTeamId() != null ? teamService.findEntityById(request.homeTeamId()) : null;
        Team awayTeam = request.awayTeamId() != null ? teamService.findEntityById(request.awayTeamId()) : null;

        match.setDate(request.date());
        match.setHomePlayer(homePlayer);
        match.setAwayPlayer(awayPlayer);
        match.setHomeTeam(homeTeam);
        match.setAwayTeam(awayTeam);
        match.setHomeTeamName(resolveTeamName(homeTeam, request.homeTeamName(), "casa"));
        match.setAwayTeamName(resolveTeamName(awayTeam, request.awayTeamName(), "visitante"));
        match.setHomeScore(request.homeScore());
        match.setAwayScore(request.awayScore());
        match.setDuration(request.duration());
        match.setGameMode(request.gameMode());
        match.setNotes(request.notes());

        return matchMapper.toResponse(match);
    }

    @Transactional
    public void delete(Long id) {
        matchRepository.delete(findEntityById(id));
    }

    private Match findEntityById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com id: " + id));
    }

    private Player findPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado com id: " + id));
    }

    private String resolveTeamName(Team team, String providedName, String lado) {
        if (team != null) {
            return team.getName();
        }
        if (!StringUtils.hasText(providedName)) {
            throw new IllegalArgumentException(
                    "Informe o nome do time (%s) ou um teamId válido do catálogo".formatted(lado));
        }
        return providedName;
    }
}