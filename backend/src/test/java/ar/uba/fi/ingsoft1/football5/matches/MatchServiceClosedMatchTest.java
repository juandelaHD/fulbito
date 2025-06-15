package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleDTO;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleStatus;
import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamCreateDTO;
import ar.uba.fi.ingsoft1.football5.teams.TeamService;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceClosedMatchTest {

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;
    
    @Mock
    private UserService userService;

    @Mock
    private TeamService teamService;

    @Mock
    private FieldService fieldService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private MatchInvitationService matchInvitationService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private MatchService matchService;

    @Mock
    private AvatarImage avatarImage;

    private User organizer;
    private User playerA;
    private User playerB;
    private User playerC;
    private Team homeTeam;
    private Team awayTeam;

    private static final long FIELD_ID = 1l;
    private static final long HOME_TEAM_ID = 1l;
    private static final long AWAY_TEAM_ID = 2l;
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;

    @BeforeEach
    void setUp() throws ItemNotFoundException {
        // Users
        organizer = new User("organizer", "Org", "anizer", "M", "Zone", 30, "pass", Role.USER);
        playerA = new User("playerA", "jorge", "A", "M", "ZoneA", 33, "pass", Role.USER);
        playerB = new User("playerB", "juan", "B", "M", "ZoneB", 28, "pass", Role.USER);
        playerC = new User("playerC", "agustin", "C", "Other", "ZoneC", 42, "pass", Role.USER);

        AvatarImage avatar = mock(AvatarImage.class);
        organizer.setAvatar(avatar);
        playerA.setAvatar(avatar);
        playerB.setAvatar(avatar);
        playerC.setAvatar(avatar);

        // Teams
        homeTeam = new Team("homeTeam", organizer);
        homeTeam.setId(HOME_TEAM_ID);
        homeTeam.addMember(organizer);
        homeTeam.addMember(playerA);

        awayTeam = new Team("awayTeam", playerB);
        awayTeam.setId(AWAY_TEAM_ID);
        awayTeam.addMember(playerB);
        awayTeam.addMember(playerC);
        //  Field
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);
        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        // Auth
        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);
    }

    @Test
    void createClosedMatch_givenValidTeams_reservesScheduleAndSendsMail() throws Exception {

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));
        when(scheduleService.markAsReserved(any(Field.class), any(), any(), any()))
                .thenReturn(new ScheduleDTO(1L, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), ScheduleStatus.RESERVED));

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                AWAY_TEAM_ID,
                MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MatchDTO closedMatch = matchService.createMatch(dto, userDetails);

        assertAll(
                () -> assertEquals(organizer.getUsername(), closedMatch.organizer().username()),
                () -> assertEquals(homeTeam.getMembers().size() + awayTeam.getMembers().size() , closedMatch.players().size()),
                () -> assertEquals(homeTeam.getMembers().size(), awayTeam.getMembers().size()),
                () -> assertEquals(homeTeam.getId(), closedMatch.homeTeam().id()),
                () -> assertEquals(awayTeam.getId(), closedMatch.awayTeam().id()),
                () -> assertNotEquals(closedMatch.homeTeam().members(), closedMatch.awayTeam().members()),
                () -> assertNotEquals(closedMatch.homeTeam().id(), closedMatch.awayTeam().id()),
                () -> assertNotEquals(homeTeam.getCaptain(), awayTeam.getCaptain())
        );

        verify(emailSenderService).sendReservationMail(eq(organizer.getUsername()), any(), any(), any());
        verify(scheduleService).markAsReserved(any(Field.class), any(), any(), any());
    }

    @Test
    void createClosedMatch_givenInvalidHomeTeam() throws Exception {
        long FALSE_TEAM_ID = 5l;
        when(teamRepository.findById(FALSE_TEAM_ID)).thenReturn(Optional.empty());
        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                FALSE_TEAM_ID,
                AWAY_TEAM_ID,
                MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        }, "expected exception due to invalid Home Team ID");
    }

    @Test
    void createClosedMatch_givenInvalidAwayTeam() throws Exception {
        long FALSE_TEAM_ID = 5l;
        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(FALSE_TEAM_ID)).thenReturn(Optional.empty());
        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                FALSE_TEAM_ID,
                MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        }, "expected exception due to invalid Away Team ID");
    }
}