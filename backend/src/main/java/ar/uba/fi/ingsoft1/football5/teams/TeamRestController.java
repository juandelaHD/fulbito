package ar.uba.fi.ingsoft1.football5.teams;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/teams")
@Tag(name = "4 - Teams", description = "Endpoints for managing teams")
public class TeamRestController {

    private final TeamService teamService;
    private final Validator validator;

    public TeamRestController(TeamService teamService, Validator validator) {
        this.teamService = teamService;
        this.validator = validator;
    }

    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new team",
            description = "Creates a new team with a unique name. The authenticated user will be the captain. Accepts a JSON string for the team and an optional image file.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Team created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TeamDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data or team name already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = """
                    {
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Team creation failed",
                      "path": "/teams/create"
                    }
                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<TeamDTO> createTeam(
            @RequestParam("team")
            @Parameter(
                    description = "TeamCreateDTO JSON payload",
                    schema = @Schema(type = "string", format = "json", implementation = TeamCreateDTO.class)
            ) String teamJson,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws IOException, IllegalArgumentException {
        ObjectMapper objectMapper = new ObjectMapper();
        TeamCreateDTO dto = objectMapper.readValue(teamJson, TeamCreateDTO.class);

        Set<ConstraintViolation<TeamCreateDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<TeamCreateDTO> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException("The provided team data is invalid: " + errorMessage.toString());
        }

        TeamDTO created = teamService.createTeam(dto, userDetails.username(), image);
        return ResponseEntity.ok(created);

    }

    @GetMapping(value = "/owned", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List teams where the user is captain",
            description = "Returns all teams where the authenticated user is the captain.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teams listed successfully",
                            content = @Content(schema = @Schema(implementation = TeamDTO.class))
                    )
            }
    )
    public List<TeamDTO> getMyTeams(@AuthenticationPrincipal JwtUserDetails userDetails) {
        return teamService.getTeamsByCaptain(userDetails.username());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List all teams",
            description = "Returns all teams in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teams listed successfully",
                            content = @Content(schema = @Schema(implementation = TeamDTO.class))
                    )
            }
    )
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update a team",
            description = "Updates the details of a team. The authenticated user must be the captain of the team.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Team updated successfully",
                            content = @Content(schema = @Schema(implementation = TeamDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Team not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Not the captain or invalid data",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable("id") Long id,
            @RequestParam("team")
            @Parameter(
                    description = "Payload JSON del equipo",
                    schema = @Schema(type = "string", format = "json", implementation = TeamCreateDTO.class)
            ) String teamJson,
            @RequestPart(value = "image", required = false)
            @Parameter(
                    description = "Team image (opcional)",
                    schema = @Schema(type = "string", format = "binary")
            ) MultipartFile image,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TeamCreateDTO dto = objectMapper.readValue(teamJson, TeamCreateDTO.class);
        TeamDTO updated = teamService.updateTeam(id, dto, userDetails.username(), image);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping(path = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Get team data",
			description = "Get team data by team id.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Get team data by team id",
							content = @Content(schema = @Schema(implementation = TeamDTO.class))
					)
			}
	)
	public TeamDTO getTeamById(
		@Parameter(description = "Team ID", required = true) @PathVariable @Positive Long id,
        @AuthenticationPrincipal JwtUserDetails userDetails
	) throws ItemNotFoundException {
		return teamService.getTeamById(id);
	}

	

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a team",
            description = "Deletes a team if the authenticated user is the captain.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Team deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Team not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Not the captain",
                            content = @Content
                    )
            }
    )
    public void deleteTeam(
            @Parameter(description = "Team ID", required = true) @PathVariable @Positive Long id,
            @AuthenticationPrincipal JwtUserDetails userDetails) throws ItemNotFoundException {
        teamService.deleteTeam(id, userDetails.username());
    }

    @PostMapping(value = "/{teamId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
        @Operation(
                summary = "Upload or replace team image",
                description = "Allows the captain to upload or replace the team's image. Use multipart/form-data with a 'file' field.",
                responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image uploaded successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Team not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Not the captain or invalid file",
                            content = @Content
                    )
            }
    )
    public void uploadTeamImage(
        @Parameter(description = "Team ID", required = true) @PathVariable @Positive Long teamId,
        @Parameter(description = "Image file", required = true, content = @Content(
                mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                schema = @Schema(type = "string", format = "binary")
        )) @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws IOException, ItemNotFoundException {
        teamService.uploadTeamImage(teamId, file, userDetails.username());
    }

    @PostMapping("/{teamId}/members")
    @Operation(
            summary = "Add a member to a team",
            description = "Allows the captain to add a user as a team member.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member added successfully"),
                    @ApiResponse(responseCode = "400", description = "Not authorized or invalid data"),
                    @ApiResponse(responseCode = "404", description = "Team or user not found")
            }
    )
    public ResponseEntity<TeamDTO> addMember(
            @PathVariable Long teamId,
            @RequestParam String username,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException, IllegalArgumentException {
        TeamDTO updated = teamService.addMember(teamId, username, userDetails.username());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{teamId}/members/{username}")
    @Operation(
            summary = "Remove a member from the team",
            description = "Allows the captain to remove a user as a team member.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Member removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Not authorized or invalid data"),
                    @ApiResponse(responseCode = "404", description = "Team or user not found")
            }
    )
    public ResponseEntity<TeamDTO> removeMember(
            @PathVariable Long teamId,
            @RequestParam String username,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws ItemNotFoundException, IllegalArgumentException {
        TeamDTO updated = teamService.removeMember(teamId, username, userDetails.username());
        return ResponseEntity.ok(updated);
    }
}