package ar.uba.fi.ingsoft1.football5.teams;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for creating or updating a team")
public record TeamCreateDTO(
        @NotBlank(message = "The team name is required")
        @Schema(description = "Team name", example = "The Galactic")
        String name,

        @Schema(description = "Team  main color", example = "Red")
        String mainColor,

        @Schema(description = "Team Secondary color", example = "White")
        String secondaryColor,

        @Schema(description = "Team level or ranking", example = "5")
        Integer ranking
) {}