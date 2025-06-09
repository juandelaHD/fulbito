package ar.uba.fi.ingsoft1.football5.teams;

import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "DTO for exposing team data")
public record TeamDTO(
        @NotNull(message = "Team ID is required")
        @Schema(description = "Team ID", example = "1")
        Long id,

        @NotBlank(message = "Team name is required")
        @Schema(description = "Team name", example = "The Galactic")
        String name,

        @NotNull(message = "TeamUrl must not be blank")
        @Schema(description = "URL of the team image", example = "https://example.com/team-image.png")
        String imageUrl,

        @NotBlank(message = "Main color is required")
        @Schema(description = "Main color", example = "Red")
        String mainColor,

        @NotBlank(message = "Secondary color is required")
        @Schema(description = "Secondary color", example = "White")
        String secondaryColor,

        @NotNull(message = "Ranking is required")
        @PositiveOrZero(message = "Ranking must be 0 or greater")
        @Schema(description = "Ranking", example = "1000")
        Integer ranking,

        @NotNull(message = "Captain is required")
        @Schema(description = "Team captain")
        UserDTO captain,

        @NotNull(message = "Members list is required")
        @Size(min = 1, message = "At least one member is required")
        @Schema(description = "Team members")
        List<UserDTO> members
) {
    public TeamDTO(Team team) {
        this(
                team.getId(),
                team.getName(),
                "/images/" + team.getImage().getId(),
                team.getMainColor(),
                team.getSecondaryColor(),
                team.getRanking(),
                new UserDTO(team.getCaptain()),
                team.getMembers().stream().map(UserDTO::new).toList()
        );
    }
}