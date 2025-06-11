package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.teams.formation.TeamFormationRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@Tag(name = "3 - Matches", description = "Endpoints for managing football matches")
public class MatchRestController {
    private final MatchService matchService;

    @Autowired
    MatchRestController(MatchService matchService,
                        MatchInvitationService matchInvitationService) {
        this.matchService = matchService;
    }

    @GetMapping(path = "/{matchId}", produces = "application/json")
    @Operation(summary = "Get match details by ID")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Match details retrieved successfully")
    MatchDTO getMatch(@Valid @PathVariable Long matchId) throws ItemNotFoundException {
        return matchService.getMatchById(matchId);
    }

    @GetMapping(path = "/{matchId}/link-invite")
    @Operation(summary = "Get invitation link for a match")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Invitation link retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Match not found")
    public String getInvitationLink(@PathVariable Long matchId) throws ItemNotFoundException {
        String token = matchService.getMatchInvitationLink(matchId);
        return "http://localhost:30003/invite/" + token; // TODO: Replace with actual URL generation logic
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new match")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Match created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid match type or parameters")
    @ApiResponse(responseCode = "404", description = "Field or user not found")
    MatchDTO createMatch(@Valid @RequestBody MatchCreateDTO matchCreate,
                         @AuthenticationPrincipal JwtUserDetails userDetails)
            throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
            return matchService.createMatch(matchCreate, userDetails);
            //TODO: ver que pasa en caso que le den un matchType erroneo
    }

    @PostMapping("/{matchId}/join")
    @Operation(summary = "Join an open match")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Successfully joined the match")
    @ApiResponse(responseCode = "400", description = "Invalid match ID")
    @ApiResponse(responseCode = "404", description = "Match not found or user not found")
    @PreAuthorize("hasRole('USER')")
    public MatchDTO joinOpenMatch(@PathVariable Long matchId, @AuthenticationPrincipal JwtUserDetails userDetails)
            throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
        return matchService.joinOpenMatch(matchId, userDetails);
    }

    @GetMapping(path = "/open-available", produces = "application/json")
    @Operation(
            summary = "Get all currently available open matches",
            description = "Returns a list of all open matches that are scheduled, have not started yet, and still have available player slots."
    )
    @ApiResponse(responseCode = "200", description = "List of available open matches retrieved successfully")
    @ResponseStatus(HttpStatus.OK)
    public List<MatchDTO> getAvailableOpenMatches() {
        return matchService.getAvailableOpenMatches();
    }

    @PostMapping("/{matchId}/form-teams")
    @Operation(
            summary = "Form teams for a match",
            description = "Allows the organizer to form teams for a match by providing team formation details."
    )
    @PreAuthorize("hasRole('USER')")
    public MatchDTO formTeams(
            @PathVariable Long matchId,
            @RequestBody TeamFormationRequestDTO request,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ) throws Exception {
        return matchService.formTeams(matchId, request, userDetails);
    }
}



