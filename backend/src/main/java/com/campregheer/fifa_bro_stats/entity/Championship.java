package com.campregheer.fifa_bro_stats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "championships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Championship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChampionshipStatus status = ChampionshipStatus.DRAFT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "match_format", nullable = false, length = 30)
    private ChampionshipMatchFormat matchFormat;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "championship_participants",
            joinColumns = @JoinColumn(name = "championship_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @Builder.Default
    private Set<Player> participants = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}