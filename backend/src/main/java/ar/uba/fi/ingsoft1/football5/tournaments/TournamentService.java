package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public TournamentService(TournamentRepository repository, UserRepository userRepository) {
        this.tournamentRepository = repository;
        this.userRepository = userRepository;
    }

    public Tournament createTournament(TournamentCreateDTO dto, String organizerUsername) {
        if (tournamentRepository.existsByName(dto.getName()))
            throw new IllegalArgumentException("Ya existe un torneo con ese nombre");

        User organizer = userRepository.findByUsername(organizerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Organizador no encontrado"));

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de finalización debe ser posterior o igual a la de inicio");
        }

        Tournament tournament = new Tournament(dto.getName(),organizer, dto.getStartDate(), dto.getEndDate(), dto.getFormat(), 
                                                dto.getMaxTeams(), dto.getRules(), dto.getPrizes(), dto.getRegistrationFee());

        return tournamentRepository.save(tournament);
    }

    public List<TournamentResponseDTO> getAllTournaments(){
        List<Tournament> tournaments = tournamentRepository.findAllWithOrganizer();

        return tournaments.stream()
            .map(TournamentResponseDTO::new)
            .collect(Collectors.toList());
    }
}

