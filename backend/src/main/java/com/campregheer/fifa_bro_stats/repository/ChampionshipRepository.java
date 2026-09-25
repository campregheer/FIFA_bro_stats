package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.Championship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionshipRepository extends JpaRepository<Championship, Long> {
}