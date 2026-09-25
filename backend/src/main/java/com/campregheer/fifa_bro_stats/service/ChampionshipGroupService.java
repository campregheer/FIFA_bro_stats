package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.entity.Championship;
import com.campregheer.fifa_bro_stats.entity.ChampionshipGroup;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.repository.ChampionshipGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipGroupService {

    private final ChampionshipGroupRepository groupRepository;

    public List<ChampionshipGroup> generateGroups(
            Championship championship
    ) {

        List<Player> players =
                new ArrayList<>(championship.getParticipants());

        Collections.shuffle(players);

        int groupCount = calculateGroupCount(players.size());

        List<ChampionshipGroup> groups = new ArrayList<>();

        for (int i = 0; i < groupCount; i++) {

            ChampionshipGroup group = ChampionshipGroup.builder()
                    .championship(championship)
                    .name(generateGroupName(i))
                    .build();

            groups.add(group);
        }

        // Round-robin:
        // A, B, C, D, A, B, C, D...
        for (int i = 0; i < players.size(); i++) {

            ChampionshipGroup group =
                    groups.get(i % groupCount);

            group.getParticipants().add(players.get(i));
        }

        return groupRepository.saveAll(groups);
    }

    private int calculateGroupCount(int participantCount) {

        // Tentamos manter aproximadamente 4 jogadores por grupo.
        int groups = (int) Math.round(participantCount / 4.0);

        return Math.max(1, groups);
    }

    private String generateGroupName(int index) {
        return String.valueOf((char) ('A' + index));
    }
}