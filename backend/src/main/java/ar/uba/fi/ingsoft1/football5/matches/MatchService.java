package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleService;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.teams.formation.TeamFormationRequestDTO;
import ar.uba.fi.ingsoft1.football5.teams.formation.TeamFormationResult;
import ar.uba.fi.ingsoft1.football5.teams.formation.TeamFormationStrategy;
import ar.uba.fi.ingsoft1.football5.teams.formation.TeamFormationStrategyType;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
// TODO: It can be separates by OPEN and CLOSED matches logic
public class MatchService {

    private static final String MATCH_ITEM = "match";
    private static final String INVITATION_PATH = "http://localhost:30003/invite/:token";

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final FieldService fieldService;
    private final EmailSenderService emailSenderService;
    private final MatchInvitationService matchInvitationService;
    private final ScheduleService scheduleService;

    public MatchService(
            MatchRepository matchRepository,
            TeamRepository teamRepository,
            UserService userService,
            FieldService fieldService,
            EmailSenderService emailSenderService,
            MatchInvitationService matchInvitationService,
            ScheduleService scheduleService
    ) {
        this.matchRepository = matchRepository;
        this. teamRepository = teamRepository;
        this.userService = userService;
        this.fieldService = fieldService;
        this.emailSenderService = emailSenderService;
        this.matchInvitationService = matchInvitationService;
        this.scheduleService = scheduleService;
    }

    public Match loadMatchById(Long id) throws ItemNotFoundException {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(MATCH_ITEM, id));
    }

    public MatchDTO getMatchById(Long id) throws ItemNotFoundException {
        return matchRepository.findById(id)
                .map(MatchDTO::new)
                .orElseThrow(() -> new ItemNotFoundException(MATCH_ITEM, id));
    }

    // TODO: Encapsulate validations in a separate class or service
    public void validationsClosedMatch(MatchCreateDTO match, Team homeTeam, Team awayTeam)
            throws IllegalArgumentException, UserNotFoundException {

        if (match.homeTeamId().equals(match.awayTeamId())) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }
        /*
        TODO: Check if this validation is necessary...
        int homeTeamSize = homeTeam.getMembers().size();
        int awayTeamSize = awayTeam.getMembers().size();

        if (homeTeamSize + awayTeamSize < match.minPlayers()) {
            throw new IllegalArgumentException("Total players in both teams must be at least " + match.minPlayers() + ". Change the teams or the match limits.");
        }

        if (homeTeamSize + awayTeamSize > match.maxPlayers()) {
            throw new IllegalArgumentException("Total players in both teams must not exceed " + match.maxPlayers() + ". Change the teams or the match limits.");
        }

        if(homeTeamSize != awayTeamSize){
            throw new IllegalArgumentException("Team sizes mismatch: home team has " + homeTeamSize +
                    " players, but away team has " + awayTeamSize + ". Both teams must have the same number of players.");
        }

        List<String> membersA = homeTeam.getMembers().stream()
                .map(User::getUsername)
                .toList();
        List<String> membersB = awayTeam.getMembers().stream()
                .map(User::getUsername)
                .toList();

        for (String member : membersA) {
            if (membersB.contains(member)) {
                throw new IllegalArgumentException("Teams cannot have players in common: " + member);
            }
        }
        */
    }

    private void notifyReservation(MatchCreateDTO match, User user) {
        emailSenderService.sendReservationMail(
                user.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime()
        );
    }

    private void notifyFieldAdmin(MatchCreateDTO match, Field field) {
        User admin = field.getOwner();
        if (admin != null) {
            emailSenderService.sendMatchNewReservationMail(
                    admin.getUsername(),
                    match.date(),
                    match.startTime(),
                    match.endTime(),
                    field.getName()
            );
        }
    }

    private void notifyTeamCaptain(MatchCreateDTO match, User captain, User organizer) {
        emailSenderService.sendTeamCaptainMail(
                captain.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime(),
                organizer.getUsername()
        );
    }

    public MatchDTO createMatch(MatchCreateDTO match, JwtUserDetails userDetails)
            throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {

        Field field = fieldService.loadFieldById(match.fieldId());
        validateFieldForMatch(field, match);

        User organizerUser = userService.loadUserByUsername(userDetails.username());

        validateMatchTime(match);
        validatePlayersCount(match);

        Match newMatch = new Match(
                field,
                organizerUser,
                match.matchType(),
                match.minPlayers(),
                match.maxPlayers(),
                match.date(),
                match.startTime(),
                match.endTime()
        );

        if (match.matchType() == MatchType.CLOSED){
            joinClosedMatch(match, newMatch);
        }

        scheduleService.markAsReserved(
                field,
                match.date(),
                match.startTime().toLocalTime(),
                match.endTime().toLocalTime()
            );

        Match savedMatch = matchRepository.save(newMatch);

        if (match.matchType() == MatchType.OPEN) {
            matchInvitationService.createInvitation(savedMatch.getId());
        }

        notifyReservation(match, organizerUser);
        notifyFieldAdmin(match, field);

        return new MatchDTO(savedMatch);
    }

    private void validateMatchTime(MatchCreateDTO match) {
        if (match.date().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Match date cannot be in the past");
        }

        if (match.startTime().isAfter(match.endTime())) {
            throw new IllegalArgumentException("Match start time cannot be after end time");
        }
    }

    private void validatePlayersCount(MatchCreateDTO match) {

        if (match.minPlayers() < 2) {
            throw new IllegalArgumentException("Minimum number of players must be at least 2");
        }

        if (match.maxPlayers() < match.minPlayers()) {
            throw new IllegalArgumentException("Maximum number of players must be greater than or equal to minimum number of players");
        }

        if (match.maxPlayers() % 2 != 0) {
            throw new IllegalArgumentException("Maximum number of players must be even for open matches.");
        }
    }

    public MatchDTO joinOpenMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException, UserNotFoundException {

        Match match = loadMatchById(matchId);

        validateMatchIsModifiable(match);

        User user = userService.loadUserByUsername(userDetails.username());

        if (match.getType() != MatchType.OPEN)
            throw new IllegalArgumentException("Only open matches can be joined");

        if (match.getStartTime().isBefore(LocalDateTime.now())) {
            match.setStatus(MatchStatus.IN_PROGRESS);
            matchRepository.save(match);
            throw new IllegalArgumentException("Cannot join a match that already started");
        }
        if (match.getPlayers().size() >= match.getMaxPlayers()) {
            matchInvitationService.invalidateMatchInvitation(match);
            matchRepository.save(match);
            throw new IllegalArgumentException("Match is full");
        }

        if (match.getPlayers().contains(user))
            throw new IllegalArgumentException("User is already registered in the match");

        for (Match userMatch : user.getJoinedMatches()) {
            if (userMatch.getDate().equals(match.getDate()) &&
                    userMatch.getStartTime().isBefore(match.getEndTime()) &&
                    userMatch.getEndTime().isAfter(match.getStartTime())) {
                throw new IllegalArgumentException("You are already registered in a match at this time");
            }
        }

        match.addPlayer(user);

        if (match.getPlayers().size() >= match.getMaxPlayers()) {
            matchInvitationService.invalidateMatchInvitation(match);
        }

        Match savedMatch = matchRepository.save(match);

        return new MatchDTO(savedMatch);
    }

    private void joinClosedMatch(MatchCreateDTO match, Match newMatch)
            throws IllegalArgumentException, UserNotFoundException{
        Team homeTeam = teamRepository.findById(match.homeTeamId())
                .orElseThrow( () -> new IllegalArgumentException("Home team with ID " + match.homeTeamId() + " does not exist"));
        Team awayTeam = teamRepository.findById(match.awayTeamId())
                .orElseThrow( () -> new IllegalArgumentException("Away team with ID " + match.awayTeamId() + " does not exist"));
        validationsClosedMatch(match, homeTeam, awayTeam);
        newMatch.addHomeTeam(homeTeam);
        newMatch.addAwayTeam(awayTeam);
        notifyTeamCaptain(match, homeTeam.getCaptain(), newMatch.getOrganizer());
        notifyTeamCaptain(match, awayTeam.getCaptain(), newMatch.getOrganizer());
    }

    private void validateFieldForMatch(Field field, MatchCreateDTO match) throws ItemNotFoundException {
        if (!field.isEnabled()) {
            throw new IllegalArgumentException("Field is not enabled for matches");
        }

        if (!fieldService.validateFieldAvailability(
                field.getId(),
                match.date(),
                match.startTime(),
                match.endTime()
        )) {
            throw new IllegalArgumentException("Field is not available at the specified date and time");
        }
    }

    public MatchDTO formTeams(Long matchId, TeamFormationRequestDTO request, JwtUserDetails userDetails)
            throws UserNotFoundException, ItemNotFoundException, IllegalArgumentException {
        TeamFormationStrategyType strategyType;
        try {
            strategyType = TeamFormationStrategyType.valueOf(request.strategy().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid strategy type: " + request.strategy() +
                            ". Possible values are: " + TeamFormationStrategyType.ALL_VALUES + "."
            );
        }

        Match match = loadMatchById(matchId);

        if (match.getHomeTeam() != null || match.getAwayTeam() != null) {
            throw new IllegalArgumentException("Teams already formed for this match");
        }

        if (!match.getOrganizer().getUsername().equals(userDetails.username())) {
            throw new IllegalArgumentException("Only the match organizer can form teams");
        }

        if (match.getPlayers().size() < match.getMinPlayers()) {
            int actualAmountOfPlayers = match.getPlayers().size();
            throw new IllegalArgumentException("Not enough players to form teams. Missing " + (match.getMinPlayers() - actualAmountOfPlayers) + " players.");
        }

        if (match.getPlayers().size() % 2 != 0) {
            throw new IllegalArgumentException("Cannot form teams with an odd number of players");
        }

        if (match.getStatus() != MatchStatus.ACCEPTED) {
            throw new IllegalArgumentException("Match is not available to form teams, current status: " + match.getStatus());
        }

        Set<User> players = match.getPlayers();
        int teamSize = players.size() / 2;

        TeamFormationStrategy strategy;
        if (strategyType == TeamFormationStrategyType.MANUAL) {
            Set<User> teamA = request.teamAPlayerIds().stream()
                    .map(userService::loadUserById)
                    .collect(Collectors.toSet());
            Set<User> teamB = request.teamBPlayerIds().stream()
                    .map(userService::loadUserById)
                    .collect(Collectors.toSet());
            strategy = TeamFormationStrategy.getStrategy(strategyType, teamA, teamB);
        } else {
            strategy = TeamFormationStrategy.getStrategy(strategyType, null, null);
        }

        TeamFormationResult result = strategy.formTeams(players, teamSize, matchId);

        Team teamA = teamRepository.save(result.teamA());
        Team teamB = teamRepository.save(result.teamB());

        match.addHomeTeam(teamA);
        match.addAwayTeam(teamB);

        match.setStatus(MatchStatus.SCHEDULED);
        matchRepository.save(match);

        // Notificar a los jugadores de cada equipo
        for (User player : teamA.getMembers()) {
            emailSenderService.sendTeamAssignmentMail(
                    player.getUsername(),
                    teamA.getName(),
                    match.getDate(),
                    match.getStartTime(),
                    match.getEndTime()
            );
        }
        for (User player : teamB.getMembers()) {
            emailSenderService.sendTeamAssignmentMail(
                    player.getUsername(),
                    teamB.getName(),
                    match.getDate(),
                    match.getStartTime(),
                    match.getEndTime()
            );
        }
        return new MatchDTO(match);
    }

    public String getMatchInvitationLink(Long matchId) throws ItemNotFoundException {
        Match match = loadMatchById(matchId);

        if (match.getInvitation() == null || !match.getInvitation().isValid()) {
            throw new ItemNotFoundException(MATCH_ITEM, matchId);
        }

        if (match.getInvitation().getToken() == null) {
            throw new IllegalArgumentException("Match invitation token is not set");
        }
        String token = match.getInvitation().getToken();
        return INVITATION_PATH.replace(":token", token);
    }

    public List<MatchDTO> getAvailableOpenMatches() {
        List<Match> matches = matchRepository.findByTypeAndStatusInAndStartTimeAfterAndPlayers_SizeLessThan(
                List.of(MatchStatus.ACCEPTED),
                LocalDateTime.now()
        );
        return matches.stream().map(MatchDTO::new).toList();
    }


    private static void validateMatchIsModifiable(Match match) throws IllegalArgumentException {
        MatchStatus status = match.getStatus();
        if (status == MatchStatus.SCHEDULED ||
                status == MatchStatus.IN_PROGRESS ||
                status == MatchStatus.FINISHED ||
                status == MatchStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot modify match with status: " + status);
        }
    }

    public void leaveOpenMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException, UserNotFoundException {

        Match match = loadMatchById(matchId);
        User user = userService.loadUserByUsername(userDetails.username());

        // Solo partidos OPEN y en estado PENDING o ACCEPTED (no SCHEDULED ni posteriores)
        if (match.getType() != MatchType.OPEN) {
            throw new IllegalArgumentException("Only open matches can be left.");
        }
        MatchStatus status = match.getStatus();
        if (status == MatchStatus.SCHEDULED ||
                status == MatchStatus.IN_PROGRESS ||
                status == MatchStatus.FINISHED ||
                status == MatchStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot leave a match that is already " + status + ".");
        }

        if (!match.getPlayers().contains(user)) {
            throw new IllegalArgumentException("You are not registered in this match.");
        }
        match.removePlayer(user);
        matchRepository.save(match);
        emailSenderService.sendUnsubscribeMail(
                user.getUsername(),
                match.getDate(),
                match.getStartTime(),
                match.getEndTime()
        );
    }

    public MatchDTO confirmMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Match match = loadMatchById(matchId);

        if (match.getStatus() != MatchStatus.PENDING)
            throw new IllegalArgumentException("The match is not in a PENDING state.");
        Field field = match.getField();
        if (!fieldService.isFieldAdmin(field.getId(), userDetails))
            throw new IllegalArgumentException("Only the field admin can confirm an open match.");

        if (match.getType() == MatchType.CLOSED) {
            match.setStatus(MatchStatus.SCHEDULED);
        } else if (match.getType() == MatchType.OPEN) {
            match.setStatus(MatchStatus.ACCEPTED);
        } else {
            throw new IllegalArgumentException("Match type not supported for confirmation.");
        }
        match.setConfirmationSent(true);

        Match savedMatch = matchRepository.save(match);
        // Notificar al organizador
        emailSenderService.sendReservationConfirmedMail(
                savedMatch.getOrganizer().getUsername(),
                savedMatch.getDate(),
                savedMatch.getStartTime(),
                savedMatch.getEndTime()
        );

        return new MatchDTO(match);
    }

    public MatchDTO startMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Match match = loadMatchById(matchId);

        if (match.getStatus() != MatchStatus.SCHEDULED)
            throw new IllegalArgumentException("The match is not in a SCHEDULED state.");
        Field field = match.getField();
        if (!fieldService.isFieldAdmin(field.getId(), userDetails))
            throw new IllegalArgumentException("Only the field admin can start a match.");

        match.setStatus(MatchStatus.IN_PROGRESS);
        matchRepository.save(match);

        return new MatchDTO(match);
    }

    public MatchDTO finishMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Match match = loadMatchById(matchId);

        if (match.getStatus() != MatchStatus.IN_PROGRESS)
            throw new IllegalArgumentException("The match is not in an IN_PROGRESS state.");
        Field field = match.getField();
        if (!fieldService.isFieldAdmin(field.getId(), userDetails))
            throw new IllegalArgumentException("Only the field admin can finish a match.");

        match.setStatus(MatchStatus.FINISHED);

        matchRepository.save(match);

        // Notificar al organizador
        emailSenderService.sendMatchFinishedMail(
                match.getOrganizer().getUsername(),
                match.getDate(),
                match.getStartTime(),
                match.getEndTime()
        );

        return new MatchDTO(match);
    }

    public MatchDTO cancelMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        Match match = loadMatchById(matchId);

        if (match.getStatus() == MatchStatus.CANCELLED || match.getStatus() == MatchStatus.FINISHED)
            throw new IllegalArgumentException("The match is already cancelled or finished.");

        Field field = match.getField();

        if (!field.getOwner().getUsername().equalsIgnoreCase(userDetails.username()) &&
                !match.getOrganizer().getUsername().equals(userDetails.username())) {
            throw new IllegalArgumentException("Only the field admin or the match organizer can cancel a match.");
        }

        match.setStatus(MatchStatus.CANCELLED);

        if (match.getInvitation() != null) {
            matchInvitationService.invalidateMatchInvitation(match);
        }

        match.clearPlayers();
        match.clearTeams();

        matchRepository.save(match);

        scheduleService.markAsAvailable(
                field,
                match.getDate(),
                match.getStartTime().toLocalTime(),
                match.getEndTime().toLocalTime()
        );

        // Notificar al organizador
        emailSenderService.sendMatchCancelledMail(
                match.getOrganizer().getUsername(),
                match.getDate(),
                match.getStartTime(),
                match.getEndTime()
        );

        return new MatchDTO(match);
    }

    public Page<MatchDTO> getMatchesByFieldAndStatus(Long fieldId, String status, LocalDate day, JwtUserDetails userDetails, Pageable pageable) throws ItemNotFoundException {
        fieldService.isFieldAdmin(fieldId, userDetails); // Valida que sea admin
        Page<Match> matches;

        if (status == null && day == null) {
            matches = matchRepository.findByFieldId(fieldId, pageable);
        } else if (status == null) {
            matches = matchRepository.findByFieldIdAndDate(fieldId, day, pageable);
        } else {
            MatchStatus matchStatus;
            try {
                matchStatus = MatchStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid match status: " + status);
            }
            if (day == null) {
                matches = matchRepository.findByFieldIdAndStatus(fieldId, matchStatus, pageable);
            } else {
                matches = matchRepository.findByFieldIdAndStatusAndDate(fieldId, matchStatus, day, pageable);
            }
        }
        return matches.map(MatchDTO::new);
    }

    public MatchDTO changeResult(Long matchId, String result, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException {
        // Validar formato "x-y"
        if (!result.matches("\\d+-\\d+")) {
            throw new IllegalArgumentException("Format is incorrect. Expected format is 'x-y' where x and y are integers.");
        }

        // Buscar el partido
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ItemNotFoundException("Match", matchId));

        // Actualizar el resultado
        match.setResult(result);
        match.setStatus(MatchStatus.FINISHED);

        Match matchSaved = matchRepository.save(match);
        return new MatchDTO(matchSaved);
    }
}
