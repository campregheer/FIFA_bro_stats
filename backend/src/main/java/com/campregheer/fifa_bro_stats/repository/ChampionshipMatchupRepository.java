package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.ChampionshipMatchup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChampionshipMatchupRepository
        extends JpaRepository<ChampionshipMatchup, Long> {

    List<ChampionshipMatchup>
    findByRoundIdOrderByPosition(Long roundId);
}