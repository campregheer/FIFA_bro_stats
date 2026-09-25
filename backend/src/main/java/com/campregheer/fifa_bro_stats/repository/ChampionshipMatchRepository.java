package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatch;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChampionshipMatchRepository
        extends JpaRepository<ChampionshipMatch, Long> {

    List<ChampionshipMatch>
    findByChampionshipId(Long championshipId);

    List<ChampionshipMatch>
    findByChampionshipIdAndStage(
            Long championshipId,
            ChampionshipStage stage
    );

    List<ChampionshipMatch>
    findByGroupId(Long groupId);

    List<ChampionshipMatch>
    findByMatchupId(Long matchupId);

    List<ChampionshipMatch>
    findByGroupIdAndPlayedTrue(Long groupId);

    boolean existsByGroupIdAndPlayedFalse(Long groupId);

    boolean existsByChampionshipIdAndStageAndPlayedFalse(
            Long championshipId,
            ChampionshipStage stage
    );
}
