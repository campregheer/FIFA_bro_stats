package com.campregheer.fifa_bro_stats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "championship_matchups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChampionshipMatchup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "round_id", nullable = false)
    private ChampionshipRound round;

    // posição do confronto dentro da fase (define o encaixe na fase seguinte)
    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer position;

    // nulo até a fase anterior definir quem entra aqui (ex: "vencedor do Matchup 2")
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "player_a_id", nullable = true)
    private Player playerA;

    // nulo = bye (playerA passa direto)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "player_b_id", nullable = true)
    private Player playerB;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "winner_id", nullable = true)
    private Player winner;

    // preenchidos só se o confronto empatar no placar normal/agregado
    @Min(0)
    @Column(name = "penalty_score_a")
    private Integer penaltyScoreA;

    @Min(0)
    @Column(name = "penalty_score_b")
    private Integer penaltyScoreB;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChampionshipMatchupStatus status = ChampionshipMatchupStatus.PENDING;
}