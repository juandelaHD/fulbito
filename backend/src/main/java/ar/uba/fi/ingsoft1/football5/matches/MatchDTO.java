package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.fields.FieldDTO;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Data Transfer Object representing a match.")
public record MatchDTO(
        @Schema(description = "Match ID", example = "42")
        Long id,

        @Schema(description = "Field where the match will take place")
        FieldDTO field,

        @Schema(description = "Organizer of the match")
        UserDTO organizer,

        @Schema(description = "List of players in the match")
        List<UserDTO> players,

        /* TODO: Uncomment when teams are implemented
        @Schema(description = "List of teams in the match")
        List<TeamDTO> teams,
        */

        @Schema(description = "Current status of the match", example = "SCHEDULED")
        MatchStatus status,

        @Schema(description = "Type of match", example = "OPEN")
        MatchType matchType,

        @Schema(description = "Minimum number of players", example = "8")
        Integer minPlayers,

        @Schema(description = "Maximum number of players", example = "10")
        Integer maxPlayers,

        @Schema(description = "Date of the match", example = "2025-06-15")
        LocalDate date,

        @Schema(description = "Start time of the match", example = "19:00")
        LocalDateTime startTime,

        @Schema(description = "End time of the match", example = "20:00")
        LocalDateTime endTime,

        @Schema(description = "Whether confirmation was sent", example = "false")
        boolean confirmationSent

        // @Schema(description = "Match result", example = "0-0")
        // Uncomment when result is implemented

) {
    public MatchDTO(Match match) {
        this(
                match.getId(),
                new FieldDTO(match.getField()),
                new UserDTO(match.getOrganizer()),
                match.getPlayers().stream().map(UserDTO::new).toList(),
                // TODO: Uncomment when teams are implemented
                // match.getTeams().stream().map(TeamDTO::new).toList(),
                match.getStatus(),
                match.getType(),
                match.getMinPlayers(),
                match.getMaxPlayers(),
                match.getDate(),
                match.getStartTime(),
                match.getEndTime(),
                match.isConfirmationSent()
        );
    }
}
