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

import org.awaitility.Awaitility;
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
    private static final int MAX_PLAYERS = 10;

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
    }

    @Test
    void createClosedMatch_givenValidTeams_reservesScheduleAndSendsMail() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));

        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);
        
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
                () -> assertNotEquals(homeTeam.getCaptain(), awayTeam.getCaptain()),
                () -> assertEquals(closedMatch.status(), MatchStatus.PENDING)
        );

        verify(emailSenderService).sendReservationMail(eq(organizer.getUsername()), any(), any(), any());
        verify(scheduleService).markAsReserved(any(Field.class), any(), any(), any());
    }

    @Test
    void tryToCreateClosedMatch_givenInvalidHomeTeam() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);
        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

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
    void tryToCreateClosedMatch_givenInvalidAwayTeam() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);
        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

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

    @Test
    void tryToCreateClosedMatch_givenInsufficientPlayers() throws Exception {
        int AUX_MAX_PLAYERS = 2;

        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                AWAY_TEAM_ID,
                MIN_PLAYERS,
                AUX_MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        }, "expected exception due to more players in the match than the min players required");
    }

    @Test
    void tryToCreateClosedMatch_givenMoreThanNecesaryPlayers() throws Exception {
        int AUX_MIN_PLAYERS = 6;
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                AWAY_TEAM_ID,
                AUX_MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        }, "expected exception due to less players in the match than the min players required");
    }

    @Test
    void testCreateClosedMatch_fieldIsDisabled_throwsException() throws Exception{
        Field field = mock(Field.class);
        when(field.isEnabled()).thenReturn(false);

        when(fieldService.loadFieldById(1L)).thenReturn(field);

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

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Field is not enabled for matches", ex.getMessage());
    }

    @Test
    void testCreateClosedMatch_fieldUnavailableForDateAndTime_throwsException() throws Exception{
        Field field = mock(Field.class);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(any(), any(), any(), any())).thenReturn(false);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                AWAY_TEAM_ID,
                MAX_PLAYERS,
                MIN_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Field is not available at the specified date and time", ex.getMessage());
    } 

    @Test
    void testGetMatchById_notFound() {
        when(matchRepository.findById(123L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            matchService.getMatchById(123L);
        });

        assertEquals("Failed to find match with id '123'", ex.getMessage());
    } 

    @Test
    void createClosedMatch_givenInvalidField() throws Exception {
        long FALSE_FIELD_ID = 5l;
        when(fieldService.loadFieldById(FALSE_FIELD_ID)).thenThrow(new ItemNotFoundException("field", FALSE_FIELD_ID));
        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FALSE_FIELD_ID,
                HOME_TEAM_ID,
                AWAY_TEAM_ID,
                MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Failed to find field with id '5'", ex.getMessage());
    }

    @Test
    void tryToCreateClosedMatch_withTheSameTeams() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                FIELD_ID,
                HOME_TEAM_ID,
                HOME_TEAM_ID,
                MIN_PLAYERS,
                MAX_PLAYERS,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Home and away teams must be different", ex.getMessage());
    }

    @Test
    void tryToCreateClosedMatch_withHomeTeamWithOneMemberMoreThanAwayTeam() throws Exception {
        homeTeam.addMember(playerC);
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));
        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);

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

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Team sizes mismatch: home team has 3 players, but away team has 2. Both teams must have the same number of players.", 
        ex.getMessage()
        );
    }

    @Test
    void tryToCreateClosedMatch_withHomeTeamWithOneMemberLessThanAwayTeam() throws Exception {
        awayTeam.addMember(playerA);
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));
        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);

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

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Team sizes mismatch: home team has 2 players, but away team has 3. Both teams must have the same number of players.", 
        ex.getMessage()
        );
    }

    @Test
    void tryToCreateClosedMatch_withPlayersInBothTeams() throws Exception {
        homeTeam.addMember(playerC);
        awayTeam.addMember(organizer);
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(FIELD_ID);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(FIELD_ID)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));
        when(userDetails.username()).thenReturn(organizer.getUsername());
        when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);

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

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createMatch(dto, userDetails);
        });

        assertEquals("Teams cannot have players in common: organizer", ex.getMessage());
    }
}