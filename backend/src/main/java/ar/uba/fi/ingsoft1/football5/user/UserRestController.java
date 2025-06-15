package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.matches.MatchDTO;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    UserDTO getUser(@NonNull @PathVariable String username) throws UserNotFoundException {
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

    @GetMapping(path = "/me", produces = "application/json")
    @Operation(
            summary = "Get my profile",
            description = "Returns the details of the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getMyProfile(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        return userService.getUserByDetails(userDetails)
                .orElseThrow(() -> new UserNotFoundException("User not found", userDetails.username()));
    }

    @GetMapping("/me/teams")
    @Operation(
            summary = "Get my teams",
            description = "Returns the list of teams associated with the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Teams retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<TeamDTO> getMyTeams(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        return userService.getTeamsByUserDetails(userDetails);
    }

    @Transactional(readOnly = true)
    @GetMapping("/me/upcoming-matches")
    @Operation(
            summary = "Get upcoming matches which I am involved in",
            description = "Returns only the matches organized by the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Upcoming matches retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchDTO> getUpcomingMatches(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        User user = userService.loadUserByUsername(userDetails.username());
        LocalDateTime now = LocalDateTime.now();
        return user.getJoinedMatches().stream()
                .filter(match -> match.getEndTime().isAfter(now)
                        && (match.getStatus() == MatchStatus.SCHEDULED))
                .map(MatchDTO::new)
                .toList();
    }


    @GetMapping("/me/played-matches")
    @Operation(
            summary = "Get matches I played",
            description = "Returns only the matches in which the authenticated user participated as a player (not organized).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Played matches retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchHistoryDTO> getMyPlayedMatches(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        return userService.getPlayedMatchesByUser(userDetails);
    }

    @GetMapping(path = "/me/reservations", produces = "application/json")
    @Operation(
            summary = "Get my reservations",
            description = "Returns the list of matches organized (reserved) by the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservations retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchDTO> getMyReservations(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        return userService.getReservationsByUser(userDetails);
    }

    @GetMapping(path = "/me/joined-matches", produces = "application/json")
    @Operation(
            summary = "Get matches I joined (not finished)",
            description = "Returns the list of matches that the authenticated user has joined and that are not finished.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Joined matches retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public List<MatchHistoryDTO> getMyJoinedMatches(@AuthenticationPrincipal JwtUserDetails userDetails) throws UserNotFoundException {
        return userService.getJoinedMatchesByUser(userDetails);
    }
}