package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamCreateDTO;
import ar.uba.fi.ingsoft1.football5.teams.TeamService;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.User;
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
    private Match closedMatch;

    @Mock
    private User organizer;

    @Mock
    private User playerA;

    @Mock
    private User playerB;

    @Mock
    private User playerC;

    @Mock
    private Team homeTeam;
    
    @Mock
    private Team awayTeam;

    @Mock
    private AvatarImage avatarImage;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        Field field = mock(Field.class);
        AvatarImage avatar = mock(AvatarImage.class);
        //Se crean los usuarios para conformar los equipos y configuracion para los testeos
        organizer = new User("organizer", "Org", "anizer", "M", "Zone", 30, "pass", Role.USER);
        playerA = new User("playerA", "jorge", "A", "M", "ZoneA", 33, "pass", Role.USER);
        playerB = new User("playerB", "juan", "B", "M", "ZoneB", 28, "pass", Role.USER);
        playerC = new User("playerC", "agustin", "C", "Other", "ZoneC", 42, "pass", Role.USER);

        organizer.setAvatar(avatar);
        playerA.setAvatar(avatar);
        playerB.setAvatar(avatar);
        playerC.setAvatar(avatar);
        //Setean los team con 2 player para los test
        TeamCreateDTO homeTeamDTO = new TeamCreateDTO("organizer", "red", "blue", 3);
        teamService.createTeam(homeTeamDTO,"homeTeam", null);
        /* 
        homeTeam = new Team("homeTeam", organizer);
        homeTeam.setId(1l);
        homeTeam.setMainColor("red");
        homeTeam.setSecondaryColor("blue");
        homeTeam.setRanking(3);
        homeTeam.addMember(organizer);
        homeTeam.addMember(playerA);
        awayTeam = new Team("awayTeam", playerB);
        awayTeam.setId(2l);
        homeTeam.setMainColor("black");
        homeTeam.setSecondaryColor("orange");
        homeTeam.setRanking(5);
        awayTeam.addMember(playerB);
        awayTeam.addMember(playerC);
        */


        //Se crea el partido para no tener que irlo generando en cada test
        closedMatch = new Match(field, organizer, MatchStatus.PENDING, MatchType.CLOSED,
                1,
                2,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
        closedMatch.addHomeTeam(homeTeam);
        closedMatch.addAwayTeam(awayTeam);
    }
/* 
    @Test
    void testCreateClosedMatch_successful() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(1L);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.CLOSED,
                1L,
                homeTeam.getId(),
                awayTeam.getId(),
                2,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MatchDTO result = matchService.createMatch(dto, userDetails);

        assertEquals(0, result.players().size());
        assertEquals("testuser", result.organizer().username());
        verify(emailSenderService).sendReservationMail(eq("testuser"), any(), any(), any());
    }
    */
}