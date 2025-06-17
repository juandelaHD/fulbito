package ar.uba.fi.ingsoft1.football5.tournaments;

import java.time.LocalDate;

import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object representing a tournaments.")
public record TournamentResponseDTO (    
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    TournamentFormat format,
    Integer maxTeams,
    TournamentStatus status,
    String rules,
    UserDTO organizer
    ){

    public TournamentResponseDTO(Tournament t) {
        this(
            t.getId(),
            t.getName(),
            t.getStartDate(),
            t.getEndDate(),
            t.getFormat(),
            t.getMaxTeams(),
            t.getStatus(),
            t.getRules() != null ? t.getRules() : "",
            new UserDTO(t.getOrganizer())
        );
    }
}