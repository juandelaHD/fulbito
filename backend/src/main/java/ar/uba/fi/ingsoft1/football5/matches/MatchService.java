package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final FieldService fieldService;
    private final EmailSenderService emailSenderService;

    public MatchService(
            MatchRepository matchRepository,
            TeamRepository teamRepository,
            UserService userService,
            FieldService fieldService,
            EmailSenderService emailSenderService
    ) {
        this.matchRepository = matchRepository;
        this. teamRepository = teamRepository;
        this.userService = userService;
        this.fieldService = fieldService;
        this.emailSenderService = emailSenderService;
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

    public void validationsClosedMatch(MatchCreateDTO match)
        throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
            // Corroboro que los teams que recibo tengan datos
            if (match.homeTeam() == null || match.homeTeam().id() == null) {
                throw new IllegalArgumentException("Home team must be provided with a valid ID");
            }
            if (match.awayTeam() == null || match.awayTeam().id() == null) {
                throw new IllegalArgumentException("Away team must be provided with a valid ID");
            }
            if (match.homeTeam().id().equals(match.awayTeam().id())) {
                throw new IllegalArgumentException("Home and away teams must be different");
            }
            // Reviso si los equipos tienen jugadores duplicados entre si
            var membersA = match.homeTeam().members().stream().map(m -> m.username().toLowerCase()).toList();
            var membersB = match.awayTeam().members().stream().map(m -> m.username().toLowerCase()).toList();

            // Jugadores duplicados
            for (String user : membersA) {
                if (membersB.contains(user)) {
                    throw new IllegalArgumentException("User '" + user + "' is in both teams");
                }
            }
        }
    private Team loadAndValidateTeam(Long teamId, String label) {
        return teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException(label + " with ID " + teamId + " does not exist"));
    }
    private void notifyMatchCreation(MatchCreateDTO match, User user) {
            emailSenderService.sendMailToVerifyMatch(
                user.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime()
        );
    }    

    public MatchDTO createMatch(MatchCreateDTO match, JwtUserDetails userDetails)
            throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {

        Field field = fieldService.loadFieldById(match.fieldId());
        validateFieldForMatch(field, match);

        User organizerUser = userService.loadUserByUsername(userDetails.username());

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
            validationsClosedMatch(match);
            Team homeTeam = loadAndValidateTeam(match.homeTeam().id(), "Home team");
            Team awayTeam = loadAndValidateTeam(match.awayTeam().id(), "Away team");
            newMatch.addHomeTeam(homeTeam);
            newMatch.addAwayTeam(awayTeam);
            notifyMatchCreation(match, homeTeam.getCaptain());
            notifyMatchCreation(match, awayTeam.getCaptain());
        }
        notifyMatchCreation(match, organizerUser);
        newMatch.setConfirmationSent(true);

        Match savedMatch = matchRepository.save(newMatch);
        return new MatchDTO(savedMatch);
    }

    public MatchDTO joinOpenMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException, UserNotFoundException {

        Match match = loadMatchById(matchId);
        User user = userService.loadUserByUsername(userDetails.username());
        validateJoinConditions(match, user);

        match.addPlayer(user);
        return new MatchDTO(matchRepository.save(match));
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

    private void validateJoinConditions(Match match, User user) throws IllegalArgumentException {
        if (match.getType() != MatchType.OPEN)
            throw new IllegalArgumentException("Only open matches can be joined");

        if (match.getStartTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Cannot join a match that already started");

        if (match.getPlayers().size() >= match.getMaxPlayers())
            throw new IllegalArgumentException("Match is full");

        if (match.getPlayers().contains(user))
            throw new IllegalArgumentException("User is already registered in the match");
    }

    public List<MatchDTO> getAvailableOpenMatches() {
        List<Match> matches = matchRepository.findAvailableOpenMatches(LocalDateTime.now());
        return matches.stream().map(MatchDTO::new).toList();
    }

}
