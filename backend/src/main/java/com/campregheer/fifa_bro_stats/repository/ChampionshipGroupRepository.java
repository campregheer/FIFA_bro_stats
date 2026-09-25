package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.ChampionshipGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChampionshipGroupRepository
        extends JpaRepository<ChampionshipGroup, Long> {

    List<ChampionshipGroup> findByChampionshipIdOrderByName(Long championshipId);
}