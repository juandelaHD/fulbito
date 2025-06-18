package ar.uba.fi.ingsoft1.football5.tournaments;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

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
    String prizes,                    
    BigDecimal registrationFee,      
    UserDTO organizer,
    Set<TeamDTO> registeredTeams
) {
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
            t.getPrizes(),                
            t.getRegistrationFee(),              
            new UserDTO(t.getOrganizer()),
            t.getRegisteredTeams().stream().map(TeamDTO::new).collect(Collectors.toSet())
        );
    }
}