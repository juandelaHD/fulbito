package ar.uba.fi.ingsoft1.football5.teams;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO for creating or updating a team")
public record TeamCreateDTO(
        @NotBlank(message = "The team name is required")
        @Schema(description = "Team name", example = "The Galactic")
        String name,

        @Schema(description = "Color principal del equipo", example = "Rojo")
        String mainColor,

        @Schema(description = "Color secundario del equipo", example = "Blanco")
        String secondaryColor,

        @Schema(description = "Ranking o nivel del equipo", example = "5")
        Integer ranking
) {}