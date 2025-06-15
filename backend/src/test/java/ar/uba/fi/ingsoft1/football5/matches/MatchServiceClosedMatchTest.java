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
    private MatchDTO closedMatch;

    @BeforeEach
    void setUp() {
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
        homeTeam.setId(1L);
        homeTeam.addMember(organizer);
        homeTeam.addMember(playerA);

        awayTeam = new Team("awayTeam", playerB);
        awayTeam.setId(2L);
        awayTeam.addMember(playerB);
        awayTeam.addMember(playerC);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(awayTeam));

        try{
            when(scheduleService.markAsReserved(any(Field.class), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                    .thenReturn(new ScheduleDTO(
                        1l,
                        LocalDate.now(),
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 0),
                        ScheduleStatus.RESERVED 
                    ));

            Field field = mock(Field.class);
            when(field.getId()).thenReturn(1L);
            when(field.isEnabled()).thenReturn(true);
            when(fieldService.loadFieldById(1L)).thenReturn(field);
            when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);
            
            when(userDetails.username()).thenReturn(organizer.getUsername());
            when(userService.loadUserByUsername(organizer.getUsername())).thenReturn(organizer);

            MatchCreateDTO matchDTO = new MatchCreateDTO(
                    MatchType.CLOSED,
                    field.getId(),
                    homeTeam.getId(),
                    awayTeam.getId(),
                    2,
                    10,
                    LocalDate.now().plusDays(1),
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(2)
            );
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            closedMatch = matchService.createMatch(matchDTO, userDetails);
        } catch (ItemNotFoundException e){
                throw new RuntimeException(e);
            }
        
    }

    @Test
    void testCreateClosedMatch_successful() throws Exception {
        assertEquals(organizer.getUsername(), closedMatch.organizer().username());
        assertEquals(4, closedMatch.players().size());
        verify(emailSenderService).sendReservationMail(eq(organizer.getUsername()), any(), any(), any());
    }

}