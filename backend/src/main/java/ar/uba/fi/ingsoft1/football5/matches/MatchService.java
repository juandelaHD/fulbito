package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserService userService;
    private final FieldService fieldService;
    private final EmailSenderService emailSenderService;
    private final MatchInvitationService matchInvitationService;

    public MatchService(
            MatchRepository matchRepository,
            UserService userService,
            FieldService fieldService,
            EmailSenderService emailSenderService,
            MatchInvitationService matchInvitationService
    ) {
        this.matchRepository = matchRepository;
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

    public MatchDTO createOpenMatch(MatchCreateDTO match, JwtUserDetails userDetails)
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
        newMatch.addPlayer(organizerUser);

        emailSenderService.sendMailOfMatchScheduled(
                organizerUser.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime()
        );
        newMatch.setConfirmationSent(true);

        Match savedMatch = matchRepository.save(newMatch);

        matchInvitationService.createInvitation(savedMatch.getId());
        return new MatchDTO(savedMatch);
    }

    public MatchDTO joinOpenMatch(Long matchId, JwtUserDetails userDetails)
            throws ItemNotFoundException, IllegalArgumentException, UserNotFoundException {

        Match match = loadMatchById(matchId);
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
            throw new IllegalArgumentException("Match is full");
        }

        if (match.getPlayers().contains(user))
            throw new IllegalArgumentException("User is already registered in the match");

        match.addPlayer(user);

        if (match.getPlayers().size() >= match.getMaxPlayers()) {
            match.setStatus(MatchStatus.COMPLETED);
            matchInvitationService.invalidateMatchInvitation(match);
        }

        Match savedMatch = matchRepository.save(match);

        return new MatchDTO(savedMatch);
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
        List<Match> matches = matchRepository.findAvailableOpenMatches(LocalDateTime.now());
        return matches.stream().map(MatchDTO::new).toList();
    }

}
