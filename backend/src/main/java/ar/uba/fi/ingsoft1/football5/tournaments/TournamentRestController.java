package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
@Tag(name = "6 - Tournaments", description = "Endpoints for managing footbal tournaments")
public class TournamentRestController {
    private final TournamentService tournamentService;

    @Autowired
    TournamentRestController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Create a new tournament")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Tournament created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid tournament parameters")
    @ApiResponse(responseCode = "404", description = "Organizer not found")
    public ResponseEntity<TournamentResponseDTO> createTournament(
            @RequestBody @Valid TournamentCreateDTO dto,
            @AuthenticationPrincipal JwtUserDetails userDetails)             
            throws UserNotFoundException {
            Tournament created = tournamentService.createTournament(dto, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TournamentResponseDTO(created));
    }

    @GetMapping(path = "/available", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of tournaments retrieved successfully")
    public List<TournamentResponseDTO> getTournaments(
        @RequestParam(required = false) String organizerUsername,
        @RequestParam(required = false) Boolean openForRegistration
    ) {
        return tournamentService.getTournamentsFiltered(organizerUsername, openForRegistration);
    }

    @GetMapping(path = "/organized-Tournaments", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of tournaments retrieved successfully")
    public List<TournamentResponseDTO> getTournamentsOrganized(            
        @AuthenticationPrincipal JwtUserDetails userDetails) {
        return tournamentService.getTournamentsOrganized(userDetails);
    }

    @GetMapping(path = "/organized-Tournaments", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of tournaments retrieved successfully")
    public List<TournamentResponseDTO> getTournamentsOrganizedBy(            
        @RequestParam String organizerUsername) {
        return tournamentService.getTournamentsOrganizedBy(organizerUsername);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tournament updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid tournament parameters"),
        @ApiResponse(responseCode = "404", description = "Tournament or user not found")
    })
    public ResponseEntity<TournamentResponseDTO> updateTournament(
        @PathVariable("id") Long tournamentId,
        @AuthenticationPrincipal JwtUserDetails userDetails,  
        @Valid @RequestBody TournamentCreateDTO dto)
        throws UserNotFoundException, ItemNotFoundException {
        TournamentResponseDTO updated =  tournamentService.updateTournament(tournamentId, dto, userDetails);
        return  ResponseEntity.ok(updated);
    }

    @PostMapping("/{tournamentId}/register")
    @Operation(summary = "Register a team to a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Team successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid team or tournament"),
        @ApiResponse(responseCode = "404", description = "Tournament or team not found"),
        @ApiResponse(responseCode = "409", description = "Team already registered")
    })
    public ResponseEntity<?> registerTeamToTournament(
        @PathVariable Long tournamentId,
        @RequestParam Long teamId,
        @AuthenticationPrincipal JwtUserDetails userDetails) throws ItemNotFoundException {
            tournamentService.registerTeam(tournamentId,teamId, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body("Team successfully registered");
        }

    @DeleteMapping("/{tournamentId}")
    @Operation(summary = "Delete a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tournament deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Confirmation flag not set"),
        @ApiResponse(responseCode = "404", description = "Tournament not found")
    })
    public ResponseEntity<?> deleteTournament(
        @PathVariable Long tournamentId,
        @RequestParam boolean confirm,
        @AuthenticationPrincipal JwtUserDetails userDetails) throws ItemNotFoundException{
            tournamentService.deleteTournament(tournamentId, userDetails, confirm);
            return ResponseEntity.ok("Tournament successfully deleted");
        }

    @PostMapping("/{tournamentId}/unregister")
    @Operation(summary = "Unregister a team of a tournament")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Team successfully unregistered"),
        @ApiResponse(responseCode = "400", description = "Invalid team or tournament"),
        @ApiResponse(responseCode = "404", description = "Tournament or team not found"),
        @ApiResponse(responseCode = "409", description = "Team is not registered in the tournament")
    })
    public ResponseEntity<?> unregisterTeamToTournament(
        @PathVariable Long tournamentId,
        @RequestParam Long teamId,
        @AuthenticationPrincipal JwtUserDetails userDetails) throws ItemNotFoundException {
            tournamentService.unregisterTeam(tournamentId,teamId, userDetails);
            return ResponseEntity.ok("Team successfully unregistered");
        }
}