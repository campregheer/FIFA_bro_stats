package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}