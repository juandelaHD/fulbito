package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final FieldService fieldService;
    private final EmailSenderService emailSenderService;
    private final MatchInvitationService matchInvitationService;

    public MatchService(
            MatchRepository matchRepository,
            TeamRepository teamRepository,
            UserService userService,
            FieldService fieldService,
            EmailSenderService emailSenderService,
            MatchInvitationService matchInvitationService
    ) {
        this.matchRepository = matchRepository;
        this. teamRepository = teamRepository;
        this.userService = userService;
        this.fieldService = fieldService;
        this.emailSenderService = emailSenderService;
        this.matchInvitationService = matchInvitationService;
    }

    public Match loadMatchById(Long id) throws ItemNotFoundException {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("match", id));
    }

    public MatchDTO getMatchById(Long id) throws ItemNotFoundException {
        return matchRepository.findById(id)
                .map(MatchDTO::new)
                .orElseThrow(() -> new ItemNotFoundException("match", id));
    }

    public void validationsClosedMatch(MatchCreateDTO match, Team homeTeam, Team awayTeam)
        throws IllegalArgumentException, UserNotFoundException {

        if (match.homeTeamId().equals(match.awayTeamId())) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }

        if (homeTeam.getMembers().size() + awayTeam.getMembers().size() < match.minPlayers()) {
            throw new IllegalArgumentException("Total players in both teams must be at least " + match.minPlayers() + ". Change the teams or the match limits.");
        }

        if (homeTeam.getMembers().size() + awayTeam.getMembers().size() > match.maxPlayers()) {
            throw new IllegalArgumentException("Total players in both teams must not exceed " + match.maxPlayers() + ". Change the teams or the match limits.");
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
    }

    private void notifyReservation(MatchCreateDTO match, User user) {
        emailSenderService.sendReservationMail(
                user.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime()
        );
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

        if (match.date().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Match date cannot be in the past");
        }

        if (match.startTime().isAfter(match.endTime())) {
            throw new IllegalArgumentException("Match start time cannot be after end time");
        }

        if (match.minPlayers() < 2 || match.maxPlayers() < match.minPlayers()) {
            throw new IllegalArgumentException("Invalid number of players for the match");
        }

        if (match.maxPlayers() % 2 != 0) {
            throw new IllegalArgumentException("Maximum number of players must be even for open matches");
        }

        Match newMatch = new Match(
                field,
                organizerUser,
                MatchStatus.SCHEDULED,
                match.matchType(),
                match.minPlayers(),
                match.maxPlayers(),
                match.date(),
                match.startTime(),
                match.endTime()
        );

        if(match.matchType() == MatchType.CLOSED){
            joinClosedMatch(match, newMatch);
        }

        notifyReservation(match, organizerUser);
        newMatch.setConfirmationSent(true);

        Match savedMatch = matchRepository.save(newMatch);

        if (match.matchType() == MatchType.OPEN) {
            matchInvitationService.createInvitation(savedMatch.getId());
        }
        return new MatchDTO(savedMatch);
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
            match.setStatus(MatchStatus.COMPLETED);
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
            match.setStatus(MatchStatus.COMPLETED);
            matchInvitationService.invalidateMatchInvitation(match);
        }

        Match savedMatch = matchRepository.save(match);

        return new MatchDTO(savedMatch);
    }

    private void joinClosedMatch(MatchCreateDTO match, Match newMatch)
            throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException{
        Team homeTeam = teamRepository.findById(match.homeTeamId())
                .orElseThrow( () -> new IllegalArgumentException("Home team with ID " + match.homeTeamId() + " does not exist"));
        Team awayTeam = teamRepository.findById(match.awayTeamId())
                .orElseThrow( () -> new IllegalArgumentException("Away team with ID " + match.homeTeamId() + " does not exist"));
        validationsClosedMatch(match, homeTeam, awayTeam);
        newMatch.addHomeTeam(homeTeam);
        newMatch.addAwayTeam(awayTeam);
        notifyTeamCaptain(match, homeTeam.getCaptain(), newMatch.getOrganizer());
        notifyTeamCaptain(match, awayTeam.getCaptain(), newMatch.getOrganizer());
        newMatch.setStatus(MatchStatus.COMPLETED);
    }

    private void validateFieldForMatch(Field field, MatchCreateDTO match) {
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

        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new IllegalArgumentException("Match is available to form teams, current status: " + match.getStatus());
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

        matchRepository.save(match);
        return new MatchDTO(match);
    }

    public String getMatchInvitationLink(Long matchId) throws ItemNotFoundException {
        Match match = loadMatchById(matchId);
        if (match.getInvitation() == null || !match.getInvitation().isValid()) {
            throw new ItemNotFoundException("match", matchId);
        }
        if (match.getInvitation().getToken() == null) {
            throw new IllegalArgumentException("Match invitation token is not set");
        }
        return match.getInvitation().getToken();
    }

    public List<MatchDTO> getAvailableOpenMatches() {
        List<Match> matches = matchRepository.findByTypeAndStatusInAndStartTimeAfterAndPlayers_SizeLessThan(
                List.of(MatchStatus.SCHEDULED),
                LocalDateTime.now()
        );
        return matches.stream().map(MatchDTO::new).toList();
    }


    private static void validateMatchIsModifiable(Match match) throws IllegalArgumentException {
        MatchStatus status = match.getStatus();
        if (status == MatchStatus.IN_PROGRESS ||
                status == MatchStatus.COMPLETED ||
                status == MatchStatus.FINISHED ||
                status == MatchStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot modify match with status: " + status);
        }
    }

}
