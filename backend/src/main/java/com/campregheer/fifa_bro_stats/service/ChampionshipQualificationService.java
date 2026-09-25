package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.entity.ChampionshipGroup;
import com.campregheer.fifa_bro_stats.repository.ChampionshipGroupRepository;
import com.campregheer.fifa_bro_stats.service.qualification.QualifiedPlayer;
import com.campregheer.fifa_bro_stats.service.standing.StandingAccumulator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChampionshipQualificationService {

    private final ChampionshipGroupRepository groupRepository;
    private final ChampionshipStandingService standingService;

    public List<QualifiedPlayer> getQualifiedPlayers(Long championshipId) {
        List<ChampionshipGroup> groups =
                groupRepository.findByChampionshipIdOrderByName(championshipId);

        if (groups.isEmpty()) {
            throw new IllegalStateException("Championship does not have groups");
        }

        List<QualifiedPlayer> directlyQualified = new ArrayList<>();
        List<QualifiedPlayer> thirdPlacedPlayers = new ArrayList<>();

        for (ChampionshipGroup group : groups) {
            List<StandingAccumulator> standings =
                    standingService.calculateStandings(group.getId());

            if (standings.size() < 3) {
                throw new IllegalStateException(
                        "Group " + group.getName() + " must have at least 3 players"
                );
            }

            directlyQualified.add(toQualifiedPlayer(standings.get(0), group, 1));
            directlyQualified.add(toQualifiedPlayer(standings.get(1), group, 2));
            thirdPlacedPlayers.add(toQualifiedPlayer(standings.get(2), group, 3));
        }

        int knockoutSize = calculateNextPowerOfTwo(directlyQualified.size());
        int thirdPlacesNeeded = knockoutSize - directlyQualified.size();

        thirdPlacedPlayers.sort(
                Comparator.comparingInt(QualifiedPlayer::points).reversed()
                        .thenComparing(Comparator.comparingInt(QualifiedPlayer::goalDifference).reversed())
                        .thenComparing(Comparator.comparingInt(QualifiedPlayer::goalsFor).reversed())
                        .thenComparing(QualifiedPlayer::groupName)
                        .thenComparing(q -> q.player().getId())
        );

        List<QualifiedPlayer> result = new ArrayList<>(directlyQualified);
        result.addAll(thirdPlacedPlayers.subList(
                0, Math.min(thirdPlacesNeeded, thirdPlacedPlayers.size())
        ));
        return result;
    }

    private QualifiedPlayer toQualifiedPlayer(
            StandingAccumulator standing,
            ChampionshipGroup group,
            int position
    ) {
        return new QualifiedPlayer(
                standing.getPlayer(),
                group.getId(),
                group.getName(),
                position,
                standing.getPoints(),
                standing.getGoalDifference(),
                standing.getGoalsFor()
        );
    }

    private int calculateNextPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }
}
