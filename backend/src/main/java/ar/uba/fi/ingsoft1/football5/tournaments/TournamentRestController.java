package ar.uba.fi.ingsoft1.football5.tournaments;


import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentRestController {

    private final TournamentService service;

    public TournamentRestController(TournamentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TournamentResponseDTO> createTournament(
            @RequestBody @Valid TournamentCreateDTO dto,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        Tournament created = service.createTournament(dto, userDetails.username());

        TournamentResponseDTO response = new TournamentResponseDTO(
            created.getId(),
            created.getName(),
            created.getStartDate(),
            created.getEndDate(),
            created.getFormat(),
            created.getMaxTeams(),
            created.getStatus(),
            created.getOrganizer()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}