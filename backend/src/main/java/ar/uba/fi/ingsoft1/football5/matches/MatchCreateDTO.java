package ar.uba.fi.ingsoft1.football5.matches;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object used when creating a new match.")
public record MatchCreateDTO(
        @NotNull(message = "Match type is required")
        @Schema(description = "Type of match", example = "OPEN")
        MatchType matchType,

        @NotNull(message = "Field ID is required")
        @Schema(description = "ID of the field where the match will take place", example = "1")
        Long fieldId,

        @Schema(description = "Team ID of team A", example = "1", nullable = true)
        Long homeTeamId,

        @Schema(description = "Team ID of team B", example = "2", nullable = true)
        Long awayTeamId,

        @Min(value = 1, message = "Minimum number of players must be at least 1")
        @Schema(description = "Minimum number of players required", example = "1")
        Integer minPlayers,

        @Min(value = 1, message = "Maximum number of players must be at least 1")
        @Schema(description = "Maximum number of players allowed", example = "10")
        Integer maxPlayers,

        @NotNull(message = "Date is required")
        @Schema(description = "Date of the match", example = "2025-06-15")
        LocalDate date,

        @NotNull(message = "Start time is required")
        @Schema(description = "Start time of the match", example = "2025-06-15T19:00:00")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        @Schema(description = "End time of the match", example = "2025-06-15T19:00:00")
        LocalDateTime endTime

) {
    public MatchCreateDTO {
        if (startTime.isAfter(endTime)) {
              throw new IllegalArgumentException("Start time must be before end time");
        }

        if (startTime.isEqual(endTime)) {
                 throw new IllegalArgumentException("Start time and end time cannot be the same");
        }

        if (!startTime.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Time set for the match must be in the future");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Match date must be in the future");
        }
    }
}
