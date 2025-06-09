package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "2 - Users")
class UserRestController {

    private final UserService userService;

    UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{username}", produces = "application/json")
    @Operation(
            summary = "Get user details by username",
            description = "Returns the details of a user specified by their username.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    // TODO: When are we using this service? Is it for the user or admin profile page? Decide PreAuthorize annotation.
    UserDTO getUser(@NonNull @PathVariable String username) {
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
    }

    @GetMapping(path = "/{username}/teams", produces = "application/json")
    @Operation(
            summary = "Get teams by user",
            description = "Returns a list of teams associated with the specified user by their username.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Teams retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<TeamDTO> getTeamsByUser(
            @PathVariable String username
    ) throws UserNotFoundException {
        return userService.getTeamsByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found", username));
    }

    @GetMapping("/{username}/played-matches")
    @Operation(
            summary = "Get played matches for a user",
            description = "Devuelve solo los partidos en los que el usuario participó como jugador (no los organizados).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Played matches retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchHistoryDTO> getPlayedMatches(@PathVariable String username) throws UserNotFoundException {
        return userService.getPlayedMatches(username);
    }

    @GetMapping(path = "/{username}/reservations", produces = "application/json")
    @Operation(
            summary = "Obtener reservas realizadas por el usuario",
            description = "Devuelve la lista de partidos reservados (organizados) por el usuario.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservas obtenidas correctamente",
                            content = @Content(schema = @Schema(implementation = MatchHistoryDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado"
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchHistoryDTO> getReservationsByUser(@PathVariable String username) throws UserNotFoundException {
        return userService.getReservationsByUser(username);
    }

}