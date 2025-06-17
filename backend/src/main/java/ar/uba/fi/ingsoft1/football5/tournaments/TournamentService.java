package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
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
    private final TeamRepository teamRepository;

    public TournamentService(TournamentRepository repository, UserRepository userRepository, TeamRepository teamRepository) {
        this.tournamentRepository = repository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
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

    public void registerTeam(Long tournamentId, Long teamId, String currentUser) throws ItemNotFoundException {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Torneo no encontrado", tournamentId));


        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("El torneo no está abierto a inscripciones.");
        }

        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ItemNotFoundException("Equipo no encontrado", teamId));

        if (!team.getCaptain().getUsername().equals(currentUser)) {
            throw new UnauthorizedException("Solo el capitán puede inscribir el equipo.", null);
        }

        if (tournament.getRegisteredTeams().contains(team)) {
            throw new IllegalStateException("El equipo ya está inscrito en este torneo.");
        }

        if (tournament.isFull()) {
            throw new IllegalStateException("Ya se alcanzó el máximo de equipos.");
        }

        tournament.getRegisteredTeams().add(team);
        //TODO: enviar mensaje de registro al capitan
        tournamentRepository.save(tournament);
    }
}

