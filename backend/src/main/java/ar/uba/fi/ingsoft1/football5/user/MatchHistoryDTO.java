package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.fields.Location;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.MatchType;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "DTO representing an entry in a user's match and reservation history.")
public record MatchHistoryDTO(
        @Schema(description = "Unique match ID", example = "42")
        Long matchId,

        @Schema(description = "Match type: 'open' or 'closed'", example = "open")
        MatchType matchType,

        @Schema(description = "Status of the match (e.g., 'scheduled', 'completed', 'cancelled')", example = "completed")
        MatchStatus status,

        @Schema(description = "Date of the match", example = "2025-06-15")
        LocalDate date,

        @Schema(description = "Start time of the match", example = "19:00")
        LocalDateTime startTime,

        @Schema(description = "End time of the match", example = "20:00")
        LocalDateTime endTime,

        @Schema(description = "Name of the field where the match was played", example = "Central Field")
        String fieldName,

        @Schema(description = "Location of the field", example = "zone: CABA, address: Av. Libertador 1234")
        Location fieldLocation,

        @Schema(description = "Match result (may be null if not played or not set)", example = "5-3")
        String result,

        @Schema(description = "List of players who participated")
        List<UserDTO> players,

        @Schema(description = "List of teams that participated")
        List<TeamDTO> teams


) {
    public MatchHistoryDTO(Match match) {
        this (
                match.getId(),
                match.getType(),
                match.getStatus(),
                match.getDate(),
                match.getStartTime(),
                match.getEndTime(),
                match.getField().getName(),
                match.getField().getLocation(),
                match.getResult() != null ? match.getResult().toString() : null,
                match.getPlayers().stream().map(UserDTO::new).toList(),
                java.util.stream.Stream.of(match.getHomeTeam(), match.getAwayTeam())
                        .filter(java.util.Objects::nonNull)
                        .map(TeamDTO::new)
                        .toList()
        );
    }
}