package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.championship.request.RegisterChampionshipMatchResultRequest;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipMatchResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipParticipantResponse;
import com.campregheer.fifa_bro_stats.entity.ChampionshipMatch;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStage;
import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchup;
import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchupStatus;
import com.campregheer.fifa_bro_stats.entity.ChampionshipRound;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStatus;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchupRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipRoundRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipMatchService {

    private final ChampionshipMatchRepository championshipMatchRepository;
    private final ChampionshipMatchupRepository matchupRepository;
    private final ChampionshipRoundRepository roundRepository;
    private final ChampionshipRepository championshipRepository;

    @Transactional
    public ChampionshipMatchResponse registerResult(
            Long matchId,
            RegisterChampionshipMatchResultRequest request
    ) {

        ChampionshipMatch match = championshipMatchRepository
                .findById(matchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Championship match not found: " + matchId
                        )
                );

        validateMatch(match);

        match.setScoreA(request.scoreA());
        match.setScoreB(request.scoreB());
        match.setPlayed(true);
        match.setDate(LocalDateTime.now());

        if (match.getStage() == ChampionshipStage.KNOCKOUT) {
            resolveKnockout(match, request);
        }

        ChampionshipMatch saved =
                championshipMatchRepository.save(match);

        return toResponse(saved);
    }

    private void resolveKnockout(ChampionshipMatch match,
                                 RegisterChampionshipMatchResultRequest request) {
        ChampionshipMatchup matchup = match.getMatchup();
        if (matchup == null) {
            throw new IllegalStateException("Knockout match must belong to a matchup");
        }
        if (request.scoreA().equals(request.scoreB())) {
            if (request.penaltyScoreA() == null || request.penaltyScoreB() == null
                    || request.penaltyScoreA().equals(request.penaltyScoreB())) {
                throw new IllegalStateException("A knockout draw requires a penalty shootout winner");
            }
            matchup.setPenaltyScoreA(request.penaltyScoreA());
            matchup.setPenaltyScoreB(request.penaltyScoreB());
            matchup.setWinner(request.penaltyScoreA() > request.penaltyScoreB()
                    ? match.getPlayerA() : match.getPlayerB());
        } else {
            matchup.setWinner(request.scoreA() > request.scoreB()
                    ? match.getPlayerA() : match.getPlayerB());
        }
        matchup.setStatus(ChampionshipMatchupStatus.COMPLETED);
        matchupRepository.save(matchup);
        advanceIfRoundComplete(matchup.getRound());
    }

    private void advanceIfRoundComplete(ChampionshipRound round) {
        List<ChampionshipMatchup> matchups = matchupRepository.findByRoundIdOrderByPosition(round.getId());
        if (matchups.stream().anyMatch(m -> m.getStatus() != ChampionshipMatchupStatus.COMPLETED)) return;
        if (matchups.size() == 1) {
            var championship = round.getChampionship();
            championship.setStatus(ChampionshipStatus.FINISHED);
            championship.setFinishedAt(Instant.now());
            championshipRepository.save(championship);
            return;
        }
        ChampionshipRound nextRound = roundRepository.save(ChampionshipRound.builder()
                .championship(round.getChampionship()).name(roundName(matchups.size() / 2))
                .orderIndex(round.getOrderIndex() + 1).build());
        for (int i = 0; i < matchups.size(); i += 2) {
            Player playerA = matchups.get(i).getWinner();
            Player playerB = matchups.get(i + 1).getWinner();
            ChampionshipMatchup nextMatchup = matchupRepository.save(ChampionshipMatchup.builder()
                    .round(nextRound).position(i / 2).playerA(playerA).playerB(playerB)
                    .status(ChampionshipMatchupStatus.PENDING).build());
            championshipMatchRepository.save(ChampionshipMatch.builder()
                    .championship(round.getChampionship()).stage(ChampionshipStage.KNOCKOUT)
                    .matchup(nextMatchup).playerA(playerA).playerB(playerB).legNumber(1).played(false).build());
        }
    }

    private String roundName(int size) {
        return switch (size) {
            case 1 -> "Final";
            case 2 -> "Semifinal";
            case 4 -> "Quartas de Final";
            case 8 -> "Oitavas de Final";
            default -> "Fase de " + size;
        };
    }

    private void validateMatch(ChampionshipMatch match) {

        if (Boolean.TRUE.equals(match.getPlayed())) {
            throw new IllegalStateException(
                    "Match result has already been registered"
            );
        }

        if (match.getStage() == ChampionshipStage.GROUP_STAGE
                && match.getGroup() == null) {

            throw new IllegalStateException(
                    "Group stage match must belong to a group"
            );
        }
    }

    private ChampionshipMatchResponse toResponse(
            ChampionshipMatch match
    ) {

        var playerA = new ChampionshipParticipantResponse(
                match.getPlayerA().getId(),
                match.getPlayerA().getName()
        );

        var playerB = new ChampionshipParticipantResponse(
                match.getPlayerB().getId(),
                match.getPlayerB().getName()
        );

        return new ChampionshipMatchResponse(
                match.getId(),
                match.getStage(),
                playerA,
                playerB,
                match.getScoreA(),
                match.getScoreB(),
                match.getPlayed(),
                match.getDate()
        );
    }
}
