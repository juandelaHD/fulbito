package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class TournamentService {

    private final TournamentRepository repository;
    private final UserRepository userRepository;

    public TournamentService(TournamentRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Tournament createTournament(TournamentCreateDTO dto, String organizerUsername) {
        if (repository.existsByName(dto.getName()))
            throw new IllegalArgumentException("Ya existe un torneo con ese nombre");

        User organizer = userRepository.findByUsername(organizerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Organizador no encontrado"));

        Tournament tournament = new Tournament(dto.getName(),organizer, dto.getStartDate(), dto.getEndDate(), dto.getFormat(), 
                                                dto.getMaxTeams(), dto.getRules(), dto.getPrizes(), dto.getRegistrationFee());

        return repository.save(tournament);
    }
}
