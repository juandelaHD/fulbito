package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
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

    @Transactional
    public TournamentResponseDTO updateTournament(Long tournamentId, TournamentCreateDTO dto, String currentUser) throws ItemNotFoundException{
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Torneo no encontrado", tournamentId));

        if (!tournament.getOrganizer().getUsername().equals(currentUser)) {
            throw new UnauthorizedException("Solo el organizador del torneo puede editarlo", null);
        }

        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("El torneo no puede editarse si no está abierto para inscripción");
        }

        if (!tournament.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("El torneo no puede editarse si ya comenzó");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalStateException("No puede tener una fecha de inicio posterior a la de fin");
        }

        if (!tournament.getName().equals(dto.getName()) && tournamentRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Ya existe un torneo con ese nombre");
        }

        tournament.setName(dto.getName());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setFormat(dto.getFormat());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setRules(dto.getRules());
        tournament.setPrizes(dto.getPrizes());
        tournament.setRegistrationFee(dto.getRegistrationFee());

        tournamentRepository.save(tournament);

        return new TournamentResponseDTO(tournament);
    }
}

