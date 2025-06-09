package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationDTO;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
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
    private final MatchInvitationService matchInvitationService;
    private static final Integer MAX_INVITATION_EXPIRY_HOURS = 24;

    @Autowired
    MatchRestController(MatchService matchService,
                        MatchInvitationService matchInvitationService) {
        this.matchService = matchService;
        this.matchInvitationService = matchInvitationService;
    }

    @GetMapping(path = "/{matchId}", produces = "application/json")
    @Operation(summary = "Get match details by ID")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Match details retrieved successfully")
    MatchDTO getMatch(@Valid @PathVariable Long matchId) throws ItemNotFoundException {
        return matchService.getMatchById(matchId);
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
        if (matchCreate.matchType() == MatchType.OPEN) {
            // Validate that the match type is OPEN
            return matchService.createOpenMatch(matchCreate, userDetails);
        } else if (matchCreate.matchType() == MatchType.CLOSED) {
            // Handle private match creation logic here
            // For now, it thows an exception if the match type is CLOSED
            throw new IllegalArgumentException("Match type CLOSED is not supported yet.");
            // return matchService.createClosedMatch(matchCreate, userDetails);
        }
        return matchService.createOpenMatch(matchCreate, userDetails);
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

    @PostMapping("/{matchId}/invite")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Generate an invitation link for a match",
            description = "Allows a registered user to generate a unique invitation link for a specific match. The link can be shared with unregistered users so they can join the match after registration."
    )
    @ApiResponse(responseCode = "200", description = "Invitation link generated successfully")
    @ApiResponse(responseCode = "404", description = "Match not found")
    @ApiResponse(responseCode = "403", description = "Not authorized to generate invitations")
    @ResponseStatus(HttpStatus.OK)
    public String generateInvitation(
            @PathVariable
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "ID of the match for which the invitation is generated",
                    required = true,
                    example = "42"
            )
            Long matchId
    ) throws ItemNotFoundException {
        MatchInvitationDTO invitation = matchInvitationService.createInvitation(matchId, MAX_INVITATION_EXPIRY_HOURS);
        // TODO: Replace with actual URL generation logic
        return "http://localhost:30003/invite/" + invitation.token();
    }
}



