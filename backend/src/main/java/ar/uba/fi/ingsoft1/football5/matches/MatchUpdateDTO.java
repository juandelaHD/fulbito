package ar.uba.fi.ingsoft1.football5.matches;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO for updating a match. Allows changing the status, result, or times of a match.")
public record MatchUpdateDTO(
        @Schema(
                description = "New status for the match (IN_PROGRESS, FINISHED, CANCELLED)",
                example = "FINISHED"
        )
        MatchStatus status,

        @Schema(description = "Result of the match (optional)", example = "5-3", nullable = true)
        String result,

        @Schema(description = "New date (optional)", example = "2024-07-01", nullable = true)
        LocalDateTime date,

        @Schema(description = "New start time (optional)", example = "2024-07-01T18:00:00", nullable = true)
        LocalDateTime startTime,

        @Schema(description = "New end time (optional)", example = "2024-07-01T19:00:00", nullable = true)
        LocalDateTime endTime
) {}