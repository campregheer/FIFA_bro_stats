package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}