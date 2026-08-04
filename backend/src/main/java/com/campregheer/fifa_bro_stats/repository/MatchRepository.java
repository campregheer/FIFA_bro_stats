package com.campregheer.fifa_bro_stats.repository;

import com.campregheer.fifa_bro_stats.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE m.homePlayer.id = :playerId OR m.awayPlayer.id = :playerId ORDER BY m.date ASC")
    List<Match> findAllByPlayerId(@Param("playerId") Long playerId);

    @Query("""
            SELECT m FROM Match m
            WHERE (m.homePlayer.id = :player1Id AND m.awayPlayer.id = :player2Id)
               OR (m.homePlayer.id = :player2Id AND m.awayPlayer.id = :player1Id)
            ORDER BY m.date ASC
            """)
    List<Match> findHeadToHead(@Param("player1Id") Long player1Id, @Param("player2Id") Long player2Id);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId ORDER BY m.date ASC")
    List<Match> findAllByTeamId(@Param("teamId") Long teamId);

    List<Match> findTop5ByOrderByDateDesc();
}