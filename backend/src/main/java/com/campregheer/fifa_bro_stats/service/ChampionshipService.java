package com.campregheer.fifa_bro_stats.service;

import com.campregheer.fifa_bro_stats.dto.championship.request.CreateChampionshipRequest;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipDetailsResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipGroupResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipMatchResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipParticipantResponse;
import com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipResponse;
import com.campregheer.fifa_bro_stats.entity.Championship;
import com.campregheer.fifa_bro_stats.entity.ChampionshipGroup;
import com.campregheer.fifa_bro_stats.entity.ChampionshipMatch;
import com.campregheer.fifa_bro_stats.entity.ChampionshipStatus;
import com.campregheer.fifa_bro_stats.entity.Player;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipGroupRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipRoundRepository;
import com.campregheer.fifa_bro_stats.repository.ChampionshipMatchupRepository;
import com.campregheer.fifa_bro_stats.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;
    private final PlayerRepository playerRepository;
    private final ChampionshipMatchRepository championshipMatchRepository;
    private final ChampionshipGroupRepository championshipGroupRepository;
    private final ChampionshipRoundRepository championshipRoundRepository;
    private final ChampionshipMatchupRepository championshipMatchupRepository;

    private final ChampionshipGroupService championshipGroupService;
    private final ChampionshipGroupMatchService championshipGroupMatchService;

    // =========================================
    // CREATE
    // =========================================

    @Transactional
    public ChampionshipResponse create(CreateChampionshipRequest request) {

        Championship championship = Championship.builder()
                .name(request.name())
                .matchFormat(request.matchFormat())
                .status(ChampionshipStatus.DRAFT)
                .build();

        Championship saved = championshipRepository.save(championship);

        return toResponse(saved);
    }

    @Transactional
    public ChampionshipResponse update(Long id, CreateChampionshipRequest request) {
        Championship championship = findEntityById(id);
        validateDraft(championship);
        championship.setName(request.name());
        championship.setMatchFormat(request.matchFormat());
        return toResponse(championshipRepository.save(championship));
    }

    @Transactional
    public void delete(Long id) {
        Championship championship = findEntityById(id);
        championshipRepository.delete(championship);
    }

    // =========================================
    // FIND
    // =========================================

    public ChampionshipResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ChampionshipDetailsResponse findDetailsById(Long id) {
        Championship championship = findEntityById(id);
        List<ChampionshipGroup> groups = championshipGroupRepository
                .findByChampionshipIdOrderByName(id);
        return toDetailsResponse(championship, groups);
    }

    public List<ChampionshipResponse> findAll() {

        return championshipRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================
    // PARTICIPANTS
    // =========================================

    @Transactional
    public ChampionshipResponse addParticipant(
            Long championshipId,
            Long playerId
    ) {

        Championship championship =
                findEntityById(championshipId);

        validateDraft(championship);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Player not found: " + playerId
                        )
                );

        if (championship.getParticipants().contains(player)) {
            throw new IllegalStateException(
                    "Player is already participating in this championship"
            );
        }

        championship.getParticipants().add(player);

        return toResponse(
                championshipRepository.save(championship)
        );
    }

    @Transactional
    public ChampionshipResponse removeParticipant(
            Long championshipId,
            Long playerId
    ) {

        Championship championship =
                findEntityById(championshipId);

        validateDraft(championship);

        boolean removed = championship.getParticipants()
                .removeIf(
                        player -> player.getId().equals(playerId)
                );

        if (!removed) {
            throw new IllegalStateException(
                    "Player is not participating in this championship"
            );
        }

        return toResponse(
                championshipRepository.save(championship)
        );
    }

    // =========================================
    // START CHAMPIONSHIP
    // =========================================

    @Transactional
    public ChampionshipDetailsResponse startChampionship(
            Long championshipId
    ) {

        Championship championship =
                findEntityById(championshipId);

        validateDraft(championship);

        if (championship.getParticipants().size() < 4) {
            throw new IllegalStateException(
                    "Championship must have at least 4 participants"
            );
        }

        // 1. Sorteia e cria os grupos
        List<ChampionshipGroup> groups =
                championshipGroupService
                        .generateGroups(championship);

        // 2. Gera todas as partidas da fase de grupos
        for (ChampionshipGroup group : groups) {
            championshipGroupMatchService
                    .generateMatches(group);
        }

        // 3. Atualiza estado do campeonato
        championship.setStatus(
                ChampionshipStatus.GROUP_STAGE
        );

        championship.setStartedAt(
                Instant.now()
        );

        championshipRepository.save(championship);

        // 4. Retorna campeonato com grupos e partidas
        return toDetailsResponse(
                championship,
                groups
        );
    }

    // =========================================
    // VALIDATIONS
    // =========================================

    private void validateDraft(
            Championship championship
    ) {

        if (championship.getStatus()
                != ChampionshipStatus.DRAFT) {

            throw new IllegalStateException(
                    "Championship must be in DRAFT"
            );
        }
    }

    // =========================================
    // FIND ENTITY
    // =========================================

    private Championship findEntityById(Long id) {

        return championshipRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Championship not found: " + id
                        )
                );
    }

    // =========================================
    // BASIC RESPONSE
    // =========================================

    private ChampionshipResponse toResponse(
            Championship championship
    ) {

        var participants =
                championship.getParticipants()
                        .stream()
                        .map(player ->
                                new ChampionshipParticipantResponse(
                                        player.getId(),
                                        player.getName()
                                )
                        )
                        .collect(Collectors.toSet());

        return new ChampionshipResponse(
                championship.getId(),
                championship.getName(),
                championship.getStatus(),
                championship.getMatchFormat(),
                participants,
                championship.getCreatedAt(),
                championship.getStartedAt(),
                championship.getFinishedAt()
        );
    }

    // =========================================
    // DETAILS RESPONSE
    // =========================================

    private ChampionshipDetailsResponse toDetailsResponse(
            Championship championship,
            List<ChampionshipGroup> groups
    ) {

        List<ChampionshipGroupResponse> groupResponses =
                groups.stream()
                        .map(group -> {

                            List<ChampionshipParticipantResponse>
                                    participants =
                                    group.getParticipants()
                                            .stream()
                                            .map(player ->
                                                    new ChampionshipParticipantResponse(
                                                            player.getId(),
                                                            player.getName()
                                                    )
                                            )
                                            .toList();

                            List<ChampionshipMatchResponse>
                                    matches =
                                    championshipMatchRepository
                                            .findByGroupId(
                                                    group.getId()
                                            )
                                            .stream()
                                            .map(this::toMatchResponse)
                                            .toList();

                            return new ChampionshipGroupResponse(
                                    group.getId(),
                                    group.getName(),
                                    participants,
                                    matches
                            );
                        })
                        .toList();

        return new ChampionshipDetailsResponse(
                championship.getId(),
                championship.getName(),
                championship.getStatus(),
                championship.getMatchFormat(),
                groupResponses,
                championshipRoundRepository.findByChampionshipIdOrderByOrderIndex(championship.getId())
                        .stream()
                        .map(round -> new com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipRoundResponse(
                                round.getId(), round.getName(), round.getOrderIndex(),
                                championshipMatchupRepository.findByRoundIdOrderByPosition(round.getId())
                                        .stream()
                                        .map(matchup -> new com.campregheer.fifa_bro_stats.dto.championship.response.ChampionshipMatchupResponse(
                                                matchup.getId(), matchup.getPosition(),
                                                participantResponse(matchup.getPlayerA()),
                                                participantResponse(matchup.getPlayerB()),
                                                participantResponse(matchup.getWinner()), matchup.getStatus(),
                                                championshipMatchRepository.findByMatchupId(matchup.getId())
                                                        .stream().map(this::toMatchResponse).toList()
                                        )).toList()
                        )).toList(),
                championship.getCreatedAt(),
                championship.getStartedAt(),
                championship.getFinishedAt()
        );
    }

    // =========================================
    // MATCH RESPONSE
    // =========================================

    private ChampionshipMatchResponse toMatchResponse(
            ChampionshipMatch match
    ) {

        ChampionshipParticipantResponse playerA =
                new ChampionshipParticipantResponse(
                        match.getPlayerA().getId(),
                        match.getPlayerA().getName()
                );

        ChampionshipParticipantResponse playerB =
                new ChampionshipParticipantResponse(
                        match.getPlayerB().getId(),
                        match.getPlayerB().getName()
                );

        return new ChampionshipMatchResponse(
                match.getId(),
                match.getStage(),
                playerA,
                playerB,
                match.getScoreA(),
                match.getScoreB(),
                match.getPlayed(),
                match.getDate()
        );
    }

    private ChampionshipParticipantResponse participantResponse(Player player) {
        return player == null ? null : new ChampionshipParticipantResponse(player.getId(), player.getName());
    }
}
