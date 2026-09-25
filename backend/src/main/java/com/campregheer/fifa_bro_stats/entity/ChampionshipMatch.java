package com.campregheer.fifa_bro_stats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "championship_matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChampionshipMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_id", nullable = false)
    private Championship championship;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChampionshipStage stage;

    // Preenchido somente na fase de grupos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ChampionshipGroup group;

    // Preenchido somente no mata-mata
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchup_id")
    private ChampionshipMatchup matchup;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_a_id", nullable = false)
    private Player playerA;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_b_id", nullable = false)
    private Player playerB;

    // usado no mata-mata
    @Min(1)
    @Column(name = "leg_number")
    private Integer legNumber;

    @Column
    private LocalDateTime date;

    @Min(0)
    @Column(name = "score_a")
    private Integer scoreA;

    @Min(0)
    @Column(name = "score_b")
    private Integer scoreB;

    @Column(name = "played", nullable = false)
    @Builder.Default
    private Boolean played = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
