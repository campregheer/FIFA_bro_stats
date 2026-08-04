package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.response.*;
import com.campregheer.fifa_bro_stats.entity.Match;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.entity.Team;
import com.campregheer.fifa_bro_stats.exception.ResourceNotFoundException;
import com.campregheer.fifa_bro_stats.mapper.MatchMapper;
import com.campregheer.fifa_bro_stats.repository.MatchRepository;
import com.campregheer.fifa_bro_stats.repository.PlayerRepository;
import com.campregheer.fifa_bro_stats.repository.TeamRepository;
import com.campregheer.fifa_bro_stats.statistics.PlayerStatsCalculator;
import com.campregheer.fifa_bro_stats.statistics.TeamStatsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final PlayerStatsCalculator playerStatsCalculator;
    private final TeamStatsCalculator teamStatsCalculator;
    private final MatchMapper matchMapper;

    public PlayerStatsResponse getPlayerStats(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado com id: " + playerId));
        List<Match> matches = matchRepository.findAllByPlayerId(playerId);
        return playerStatsCalculator.calculate(playerId, player.getName(), matches);
    }

    public TeamStatsResponse getTeamStats(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Time não encontrado com id: " + teamId));
        List<Match> matches = matchRepository.findAllByTeamId(teamId);
        return teamStatsCalculator.calculate(teamId, team.getName(), matches);
    }

    public HeadToHeadResponse getHeadToHead(Long player1Id, Long player2Id) {
        Player player1 = playerRepository.findById(player1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado com id: " + player1Id));
        Player player2 = playerRepository.findById(player2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Jogador não encontrado com id: " + player2Id));

        List<Match> matches = matchRepository.findHeadToHead(player1Id, player2Id);

        PlayerStatsResponse stats1 = playerStatsCalculator.calculate(player1Id, player1.getName(), matches);
        PlayerStatsResponse stats2 = playerStatsCalculator.calculate(player2Id, player2.getName(), matches);

        return new HeadToHeadResponse(stats1, stats2, matches.size());
    }

    public List<PlayerStatsResponse> getGeneralRanking() {
        return playerRepository.findAll().stream()
                .map(player -> playerStatsCalculator.calculate(
                        player.getId(),
                        player.getName(),
                        matchRepository.findAllByPlayerId(player.getId())))
                .sorted(Comparator.comparingInt(PlayerStatsResponse::points).reversed()
                        .thenComparing(Comparator.comparingInt(PlayerStatsResponse::goalDifference).reversed()))
                .toList();
    }

    public DashboardResponse getDashboard() {
        long totalPlayers = playerRepository.count();
        long totalTeams = teamRepository.count();
        long totalMatches = matchRepository.count();

        List<PlayerStatsResponse> topRanking = getGeneralRanking().stream().limit(5).toList();

        List<MatchResponse> recentMatches = matchRepository.findTop5ByOrderByDateDesc()
                .stream()
                .map(matchMapper::toResponse)
                .toList();

        return new DashboardResponse(totalPlayers, totalTeams, totalMatches, topRanking, recentMatches);
    }
}