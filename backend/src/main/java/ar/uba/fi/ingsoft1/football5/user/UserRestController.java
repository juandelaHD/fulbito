package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import io.swagger.v3.oas.annotations.Operation;
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
}