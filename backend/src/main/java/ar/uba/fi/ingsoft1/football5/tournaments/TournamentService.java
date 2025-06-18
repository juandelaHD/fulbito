package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UnauthorizedException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final EmailSenderService emailSenderService;

    public TournamentService(TournamentRepository repository, UserRepository userRepository, TeamRepository teamRepository, EmailSenderService emailSenderService) {
        this.tournamentRepository = repository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.emailSenderService = emailSenderService;
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

        emailSenderService.sendTournamentOrganizerMail(tournament.getOrganizer().getUsername(),tournament.getStartDate(),
                                                        tournament.getEndDate(), tournament.getName());         
                                                                                               
        return tournamentRepository.save(tournament);
    }

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

        if (dto.getRegistrationFee().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Registration fee cannot be negative");
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

        emailSenderService.sendTournamentUpdatedOrganizerMail(tournament.getOrganizer().getUsername(), tournament);    
        
        for (Team team: tournament.getRegisteredTeams()){
                emailSenderService.sendTeamCaptainTournamentUpdated(team.getCaptain().getUsername(),tournament.getStartDate(),
                                                                    tournament.getEndDate(), tournament.getName());
        }

        return new TournamentResponseDTO(tournament);
    }

    public void registerTeam(Long tournamentId, Long teamId, JwtUserDetails currentUser) throws ItemNotFoundException {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Tournament not found", tournamentId));

        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("The tournament is not open for registrations");
        }

        if (tournament.isFull()) {
            throw new IllegalStateException("The maximum number of teams has been reached");
        }

        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ItemNotFoundException("Team not found", teamId));

        if (!team.getCaptain().getUsername().equals(currentUser.username())) {
            throw new UnauthorizedException("Only the captain can register the team", null);
        }

        if (tournament.getRegisteredTeams().contains(team)) {
            throw new IllegalStateException("The team is already registered in this tournament");
        }

        Set<User> registeredPlayers = new HashSet<>();
        for (Team registeredTeam : tournament.getRegisteredTeams()) {
            registeredPlayers.addAll(registeredTeam.getMembers());
        }

        for (User player : team.getMembers()) {
            if (registeredPlayers.contains(player)) {
                throw new IllegalStateException("A player in this team is already registered in another team in the tournament");
            }
        }

        tournament.getRegisteredTeams().add(team);

        emailSenderService.sendTeamCaptainTournamentMail(team.getCaptain().getUsername(),tournament.getStartDate(),tournament.getEndDate(),
                                                        tournament.getOrganizer().getUsername(), tournament.getName());
        tournamentRepository.save(tournament);
    }
    
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

        emailSenderService.sendTournamentCancelledOrganizerMail(tournament.getOrganizer().getUsername(),tournament.getStartDate(),
                                                                tournament.getEndDate(), tournament.getName());
        for (Team team: tournament.getRegisteredTeams()){
            emailSenderService.sendTeamCaptainTournamentCanceled(team.getCaptain().getUsername(),tournament.getStartDate(),
                                                                tournament.getEndDate(), tournament.getName());
        }

        tournament.clearTeams();

        tournament.setStatus(TournamentStatus.CANCELLED);
        tournamentRepository.save(tournament);
    }

    public void unregisterTeam(Long tournamentId, Long teamId, JwtUserDetails currentUser) throws ItemNotFoundException {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new ItemNotFoundException("Tournament not found", tournamentId));

        if (!tournament.getStatus().equals(TournamentStatus.OPEN_FOR_REGISTRATION)) {
            throw new IllegalStateException("Cannot unregister team. Tournament is not accepting registration changes (current status: "
                                            + tournament.getStatus() + ").");
        }

        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ItemNotFoundException("Team not found", teamId));

        if (!team.getCaptain().getUsername().equals(currentUser.username())) {
            throw new UnauthorizedException("Team unregistration can only be performed by the team captain", null);
        }

        if (!tournament.getRegisteredTeams().contains(team)) {
            throw new IllegalStateException("The team is not registered in this tournament");
        }

        emailSenderService.sendTeamCaptainUnregisterTournamentMail(team.getCaptain().getUsername(), tournament.getStartDate(),
                                                                    tournament.getEndDate(), tournament.getName());
        tournament.getRegisteredTeams().remove(team);
        tournamentRepository.save(tournament);
    }

    public List<TournamentResponseDTO> getTournamentsFiltered(String organizerUsername, Boolean openForRegistration) {
        Specification<Tournament> spec = Specification.where(null);

        if (organizerUsername != null && !organizerUsername.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.join("organizer").get("username"), organizerUsername)
            );
        }

        if (Boolean.TRUE.equals(openForRegistration)) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), TournamentStatus.OPEN_FOR_REGISTRATION)
            );
        }

        List<Tournament> tournaments;
        tournaments = tournamentRepository.findAll(spec);

        return tournaments.stream()
                .map(TournamentResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<TournamentResponseDTO> getTournamentsOrganized(JwtUserDetails currentUser){
        return tournamentRepository.findAllByOrganizerUsername(currentUser.username()).stream()
                .map(TournamentResponseDTO::new).toList();
    }

    public List<TournamentResponseDTO> getTournamentsOrganizedBy(String currentUser){
        return tournamentRepository.findAllByOrganizerUsername(currentUser).stream()
                .map(TournamentResponseDTO::new).toList();
    }
}

