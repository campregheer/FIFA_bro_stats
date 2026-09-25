package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.entity.*;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipGroupMatchService {

    private final ChampionshipMatchRepository matchRepository;

    public List<ChampionshipMatch> generateMatches(
            ChampionshipGroup group
    ) {

        List<Player> players =
                new ArrayList<>(group.getParticipants());

        List<ChampionshipMatch> matches =
                new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {

            for (int j = i + 1; j < players.size(); j++) {

                ChampionshipMatch match =
                        ChampionshipMatch.builder()
                                .championship(group.getChampionship())
                                .stage(ChampionshipStage.GROUP_STAGE)
                                .group(group)
                                .playerA(players.get(i))
                                .playerB(players.get(j))
                                .played(false)
                                .build();

                matches.add(match);
            }
        }

        return matchRepository.saveAll(matches);
    }
}