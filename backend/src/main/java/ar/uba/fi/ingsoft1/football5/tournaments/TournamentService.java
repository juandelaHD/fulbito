package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
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

    public Tournament createTournament(TournamentCreateDTO dto, JwtUserDetails currentUser) {
        if (tournamentRepository.existsByName(dto.getName()))
            throw new IllegalArgumentException("A tournament with that name already exists");

        User organizer = userRepository.findByUsername(currentUser.username())
                .orElseThrow(() -> new EntityNotFoundException("Organizador no encontrado"));

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("The end date must be the same as or later than the start date");
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
    public TournamentResponseDTO updateTournament(Long tournamentId, TournamentCreateDTO dto, JwtUserDetails currentUser) throws ItemNotFoundException{
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Tournament not found", tournamentId));

        if (!tournament.getOrganizer().getUsername().equals(currentUser.username())) {
            throw new UnauthorizedException("Only the tournament organizer can edit it", null);
        }

        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("The tournament cannot be edited unless it is open for registration");
        }

        if (!tournament.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("The tournament cannot be edited once it has started");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalStateException("The start date cannot be later than the end date");
        }

        if (!tournament.getName().equals(dto.getName()) && tournamentRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("A tournament with that name already exists");
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

    public void registerTeam(Long tournamentId, Long teamId, JwtUserDetails currentUser) throws ItemNotFoundException {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Tournament not found", tournamentId));


        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("The tournament is not open for registrations");
        }

        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ItemNotFoundException("Team not found", teamId));

        if (!team.getCaptain().getUsername().equals(currentUser.username())) {
            throw new UnauthorizedException("Only the captain can register the team", null);
        }

        if (tournament.getRegisteredTeams().contains(team)) {
            throw new IllegalStateException("The team is already registered in this tournament");
        }

        if (tournament.isFull()) {
            throw new IllegalStateException("The maximum number of teams has been reached");
        }

        tournament.getRegisteredTeams().add(team);
        //TODO: enviar mensaje de registro al capitan
        tournamentRepository.save(tournament);
    }
    
    @Transactional
    public void deleteTournament(Long tournamentId, JwtUserDetails currentUser, boolean confirmed) throws ItemNotFoundException{
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Tournament not found", tournamentId));
        
        if (!tournament.getOrganizer().getUsername().equals(currentUser.username())) {
            throw new UnauthorizedException("Only the tournament organizer can delete it", null);
        }

        if(!confirmed){
            throw new IllegalArgumentException("Explicit confirmation is required");
        }

        if(tournament.getStatus().equals(TournamentStatus.IN_PROGRESS) || tournament.getStatus().equals(TournamentStatus.FINISHED)){
            throw new IllegalArgumentException("A tournament in progress or already finished cannot be deleted");
        }
        //TODO: enviar a los capitanes y al organizador mail con que se cancelo el torneo
        tournament.clearTeams();

        tournament.setStatus(TournamentStatus.CANCELED);
        tournamentRepository.save(tournament);

    }
}

