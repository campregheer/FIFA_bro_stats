package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.ChampionshipRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChampionshipRoundRepository
        extends JpaRepository<ChampionshipRound, Long> {

    List<ChampionshipRound>
    findByChampionshipIdOrderByOrderIndex(Long championshipId);
}