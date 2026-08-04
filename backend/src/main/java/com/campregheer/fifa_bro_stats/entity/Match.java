package com.campregheer.fifa_bro_stats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime date;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_player_id", nullable = false)
    private Player homePlayer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_player_id", nullable = false)
    private Player awayPlayer;

    // Time cadastrado no catálogo (opcional)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "home_team_id", nullable = true)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "away_team_id", nullable = true)
    private Team awayTeam;

    // Nome do time sempre preenchido (vem do catálogo ou é digitado livre)
    @NotBlank
    @Column(name = "home_team_name", nullable = false, length = 100)
    private String homeTeamName;

    @NotBlank
    @Column(name = "away_team_name", nullable = false, length = 100)
    private String awayTeamName;

    @NotNull
    @Min(0)
    @Column(name = "home_score", nullable = false)
    private Integer homeScore;

    @NotNull
    @Min(0)
    @Column(name = "away_score", nullable = false)
    private Integer awayScore;

    // duração em minutos
    private Integer duration;

    @Column(name = "game_mode", length = 50)
    private String gameMode;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}