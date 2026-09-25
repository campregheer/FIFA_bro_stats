package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipParticipantResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipStandingResponse;
import com.campregheer.fifa_bro_stats.entity.ChampionshipGroup;
import com.campregheer.fifa_bro_stats.entity.ChampionshipMatch;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.repository.ChampionshipGroupRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchRepository;
import com.campregheer.fifa_bro_stats.service.standing.StandingAccumulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ChampionshipStandingService {

    private final ChampionshipGroupRepository groupRepository;
    private final ChampionshipMatchRepository matchRepository;

    // Esse continua sendo usado pelo controller
    public List<ChampionshipStandingResponse> getStandings(
            Long groupId
    ) {

        List<StandingAccumulator> standings =
                calculateStandings(groupId);

        return buildResponse(standings);
    }

    // Esse será usado internamente para descobrir os classificados
    public List<StandingAccumulator> calculateStandings(
            Long groupId
    ) {

        ChampionshipGroup group = groupRepository.findById(groupId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Championship group not found: " + groupId
                        )
                );

        Map<Long, StandingAccumulator> standings =
                new HashMap<>();

        // Cria todos os jogadores com estatísticas zeradas
        for (Player player : group.getParticipants()) {

            standings.put(
                    player.getId(),
                    new StandingAccumulator(player)
            );
        }

        // Busca somente partidas já jogadas
        List<ChampionshipMatch> matches =
                matchRepository.findByGroupIdAndPlayedTrue(groupId);

        for (ChampionshipMatch match : matches) {

            if (match.getScoreA() == null
                    || match.getScoreB() == null) {

                throw new IllegalStateException(
                        "Played match " +
                                match.getId() +
                                " does not have a valid score"
                );
            }

            StandingAccumulator playerA =
                    standings.get(
                            match.getPlayerA().getId()
                    );

            StandingAccumulator playerB =
                    standings.get(
                            match.getPlayerB().getId()
                    );

            playerA.registerMatch(
                    match.getScoreA(),
                    match.getScoreB()
            );

            playerB.registerMatch(
                    match.getScoreB(),
                    match.getScoreA()
            );
        }

        return standings.values()
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        StandingAccumulator::getPoints
                                )
                                .reversed()

                                .thenComparing(
                                        Comparator
                                                .comparingInt(
                                                        StandingAccumulator::getGoalDifference
                                                )
                                                .reversed()
                                )

                                .thenComparing(
                                        Comparator
                                                .comparingInt(
                                                        StandingAccumulator::getGoalsFor
                                                )
                                                .reversed()
                                )
                )
                .toList();
    }

    private List<ChampionshipStandingResponse> buildResponse(
            List<StandingAccumulator> standings
    ) {

        return IntStream
                .range(0, standings.size())
                .mapToObj(index -> {

                    StandingAccumulator standing =
                            standings.get(index);

                    Player player =
                            standing.getPlayer();

                    return new ChampionshipStandingResponse(
                            index + 1,

                            new ChampionshipParticipantResponse(
                                    player.getId(),
                                    player.getName()
                            ),

                            standing.getPoints(),
                            standing.getPlayed(),
                            standing.getWins(),
                            standing.getDraws(),
                            standing.getLosses(),
                            standing.getGoalsFor(),
                            standing.getGoalsAgainst(),
                            standing.getGoalDifference()
                    );
                })
                .toList();
    }
}