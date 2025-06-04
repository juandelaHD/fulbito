package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import ar.uba.fi.ingsoft1.football5.user.UserService;

import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserService userService;
    private final FieldService fieldService;
    private final EmailSenderService emailSenderService;

    public MatchService(
            MatchRepository matchRepository,
            UserService userService,
            FieldService fieldService,
            EmailSenderService emailSenderService
    ) {
        this.matchRepository = matchRepository;
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

    public MatchDTO createOpenMatch(MatchCreateDTO match) throws IllegalArgumentException, ItemNotFoundException, UserNotFoundException {
        // Validar que el tipo de partido es OPEN
        if (match.matchType() != MatchType.OPEN) {
            throw new IllegalArgumentException("Match type must be OPEN");
        }

        // Validar tiempo de inicio y fin
        if (match.startTime().isAfter(match.endTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Validar tiempo de inicio y fin
        if (match.startTime().isEqual(match.endTime())) {
            throw new IllegalArgumentException("Start time and end time cannot be the same");
        }

        // Validar fecha
        LocalDate today = LocalDate.now();
        if (!match.date().isAfter(today)) {
            throw new IllegalArgumentException("Match date must be in the future");
        }

        // Validar que el fieldID existe
        Field field = fieldService.loadFieldById(match.fieldId());

        // Validar cancha y horario
        if (!fieldService.validateFieldAvailability(
                field.getId(),
                match.date(),
                match.startTime(),
                match.endTime()
        )) {
            throw new IllegalArgumentException("Field is not available at the specified date and time");
        }

        // Validar que el usuario existe
        UserDTO organizer = userService.getUserById(match.organizerId());
        User organizerUser = userService.loadUserByUsername(organizer.username());

        // Crear el partido
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

        // Agregar el organizador a los jugadores del partido
        newMatch.addPlayer(organizerUser);

        // Enviar correo de reserva al organizador
        emailSenderService.sendMailToVerifyMatch(
                organizerUser.getUsername(),
                match.date(),
                match.startTime(),
                match.endTime()
        );
        newMatch.setConfirmationSent(true);

        // Guardar el partido
        Match savedMatch = matchRepository.save(newMatch); // Guardar de nuevo para asegurar que se actualiza la lista de jugadores

        // Retornar el DTO del partido guardado
        return new MatchDTO(savedMatch);
    }

    public MatchDTO joinOpenMatch(Long matchId, Long userId) throws ItemNotFoundException, IllegalArgumentException, UserNotFoundException {
        Match match = loadMatchById(matchId);

        User user = userService.loadUserById(userId);
        validateJoinConditions(match, user);

        match.addPlayer(user);

        return new MatchDTO(matchRepository.save(match));
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
