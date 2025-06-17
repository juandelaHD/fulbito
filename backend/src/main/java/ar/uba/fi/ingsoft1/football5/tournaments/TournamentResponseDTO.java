package ar.uba.fi.ingsoft1.football5.tournaments;

import java.time.LocalDate;

import ar.uba.fi.ingsoft1.football5.user.User;

public class TournamentResponseDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
    private Integer maxTeams;
    private TournamentStatus status;
    private User organizer;

    public TournamentResponseDTO(long id, String name, LocalDate startDate, LocalDate endDate, 
                                    String format, Integer maxTeams, TournamentStatus status, User organizer){
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.format= format;
            this.maxTeams = maxTeams;
            this.status = status;
            this.organizer = organizer;
    }
}