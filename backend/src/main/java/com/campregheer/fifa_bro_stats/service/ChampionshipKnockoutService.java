package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.championship.response.*;
import com.campregheer.fifa_bro_stats.entity.*;
import com.campregheer.fifa_bro_stats.repository.*;
import com.campregheer.fifa_bro_stats.service.qualification.QualifiedPlayer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipKnockoutService {
    private final ChampionshipRepository championshipRepository;
    private final ChampionshipRoundRepository roundRepository;
    private final ChampionshipMatchupRepository matchupRepository;
    private final ChampionshipMatchRepository matchRepository;
    private final ChampionshipQualificationService qualificationService;

    @Transactional
    public ChampionshipRoundResponse generateFirstRound(Long championshipId) {
        Championship championship = championshipRepository.findById(championshipId)
                .orElseThrow(() -> new IllegalArgumentException("Championship not found: " + championshipId));
        if (championship.getStatus() != ChampionshipStatus.GROUP_STAGE) {
            throw new IllegalStateException("Championship must be in GROUP_STAGE to generate the knockout");
        }
        if (matchRepository.existsByChampionshipIdAndStageAndPlayedFalse(championshipId, ChampionshipStage.GROUP_STAGE)) {
            throw new IllegalStateException("All group stage matches must have a result first");
        }
        if (!roundRepository.findByChampionshipIdOrderByOrderIndex(championshipId).isEmpty()) {
            throw new IllegalStateException("Knockout rounds have already been generated");
        }

        List<QualifiedPlayer> qualified = qualificationService.getQualifiedPlayers(championshipId);
        if (qualified.size() < 2 || (qualified.size() & (qualified.size() - 1)) != 0) {
            throw new IllegalStateException("Qualified players must form a power-of-two bracket");
        }

        ChampionshipRound round = roundRepository.save(ChampionshipRound.builder()
                .championship(championship).name(roundName(qualified.size())).orderIndex(1).build());
        List<ChampionshipMatchup> matchups = new ArrayList<>();
        List<ChampionshipMatch> matches = new ArrayList<>();
        for (int position = 0; position < qualified.size() / 2; position++) {
            QualifiedPlayer a = qualified.get(position);
            QualifiedPlayer b = qualified.get(qualified.size() - 1 - position);
            ChampionshipMatchup matchup = matchupRepository.save(ChampionshipMatchup.builder()
                    .round(round).position(position).playerA(a.player()).playerB(b.player())
                    .status(ChampionshipMatchupStatus.PENDING).build());
            matchups.add(matchup);
            matches.add(ChampionshipMatch.builder().championship(championship).stage(ChampionshipStage.KNOCKOUT)
                    .matchup(matchup).playerA(a.player()).playerB(b.player()).legNumber(1).played(false).build());
        }
        matchRepository.saveAll(matches);
        championship.setStatus(ChampionshipStatus.KNOCKOUT_STAGE);
        championshipRepository.save(championship);
        return toResponse(round, matchups, matches);
    }

    private String roundName(int size) {
        return switch (size) {
            case 2 -> "Final";
            case 4 -> "Semifinal";
            case 8 -> "Quartas de Final";
            case 16 -> "Oitavas de Final";
            default -> "Fase de " + size;
        };
    }

    private ChampionshipRoundResponse toResponse(ChampionshipRound round,
                                                  List<ChampionshipMatchup> matchups,
                                                  List<ChampionshipMatch> matches) {
        List<ChampionshipMatchupResponse> responses = matchups.stream().map(matchup ->
                new ChampionshipMatchupResponse(matchup.getId(), matchup.getPosition(),
                        participant(matchup.getPlayerA()), participant(matchup.getPlayerB()),
                        participant(matchup.getWinner()), matchup.getStatus(),
                        matches.stream().filter(m -> m.getMatchup().getId().equals(matchup.getId()))
                                .map(this::toMatchResponse).toList())
        ).toList();
        return new ChampionshipRoundResponse(round.getId(), round.getName(), round.getOrderIndex(), responses);
    }

    private ChampionshipMatchResponse toMatchResponse(ChampionshipMatch match) {
        return new ChampionshipMatchResponse(match.getId(), match.getStage(), participant(match.getPlayerA()),
                participant(match.getPlayerB()), match.getScoreA(), match.getScoreB(), match.getPlayed(), match.getDate());
    }

    private ChampionshipParticipantResponse participant(Player player) {
        return player == null ? null : new ChampionshipParticipantResponse(player.getId(), player.getName());
    }
}
