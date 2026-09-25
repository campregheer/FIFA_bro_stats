package com.campregheer.fifa_bro_stats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "championship_rounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChampionshipRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "championship_id", nullable = false)
    private Championship championship;

    // Ex: "Oitavas de Final", "Quartas de Final", "Semifinal", "Final"
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    // 1 = primeira fase, 2 = próxima, etc. Usado pra ordenar e gerar a próxima fase.
    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}