package ar.uba.fi.ingsoft1.football5.teams.formation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
        description = "DTO for team formation in a match. " +
                "Strategy types: MANUAL, RANDOM, BY_AGE, BY_EXPERIENCE, BY_GENDER, BY_ZONE. " +
                "For automatic strategies (RANDOM, BY_AGE, etc.), use 'allPlayerIds'. " +
                "For MANUAL strategy, use 'teamAPlayerIds' and 'teamBPlayerIds'."
)
public record TeamFormationRequestDTO(
        @Schema(
                description = "Name of the team formation strategy.",
                example = "RANDOM",
                required = true,
                allowableValues = {"MANUAL", "RANDOM", "BY_AGE", "BY_EXPERIENCE", "BY_GENDER", "BY_ZONE"}
        )
        String strategy,

        @Schema(
                description = "Player IDs for Team A (only for MANUAL strategy). Optional.",
                example = "[1,2,3,4,5]",
                nullable = true
        )
        List<Long> teamAPlayerIds,

        @Schema(
                description = "Player IDs for Team B (only for MANUAL strategy). Optional.",
                example = "[6,7,8,9,10]",
                nullable = true
        )
        List<Long> teamBPlayerIds
) {}