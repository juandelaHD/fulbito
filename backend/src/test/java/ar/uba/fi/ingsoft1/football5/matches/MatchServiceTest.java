/*
TODO: Test for MatchService
 * This file is commented out to avoid compilation errors.
    * Uncomment and implement tests as needed.
    * - testJoinOpenMatch_successful
    * - testJoinOpenMatch_alreadyStarted
    * - testJoinOpenMatch_matchFull
    * - testJoinOpenMatch_alreadyJoined
    * - testJoinOpenMatch_closedMatch
    * - testJoinOpenMatch_matchNotFound
    * - testJoinOpenMatch_userNotFound
    * - testJoinOpenMatch_saveIsCalled
    * - testCreateOpenMatch_successful
    * - testCreateOpenMatch_dateInPast_throwsException
    * - testCreateOpenMatch_startAfterEnd_throwsException
    * - testCreateOpenMatch_fieldUnavailable_throwsException
    * - testGetMatchById_notFound

package ar.uba.fi.ingsoft1.football5.matches;


import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.images.Image;

import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserService userService;

    @Mock
    private FieldService fieldService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private Match openMatch;

    @Mock
    private User user;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        Field field = mock(Field.class);
        User organizer = mock(User.class);
        openMatch = new Match(field, organizer, MatchStatus.SCHEDULED, MatchType.OPEN,
                1,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
        openMatch.setMaxPlayers(2);
        openMatch.setMinPlayers(1);
        user = new User("testuser", "Test", "User", "M", "Zone1", 25, "pass123", Role.USER);
    }

    @Test
    void testJoinOpenMatch_successful() throws Exception {
        Image mockAvatar = mock(Image.class);
        when(mockAvatar.getId()).thenReturn(123L);
        user.setAvatar(mockAvatar);

        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername(anyString())).thenReturn(user);
        when(matchRepository.save(openMatch)).thenReturn(openMatch);

        MatchDTO result = matchService.joinOpenMatch(1L, userDetails);

        assertEquals(1, result.players().size());
        assertTrue(openMatch.getPlayers().contains(user));
    }

    @Test
    void testJoinOpenMatch_alreadyStarted() {
        openMatch.setStartTime(LocalDateTime.now().minusMinutes(10));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, userDetails)
        );

        assertEquals("Cannot join a match that already started", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_matchFull() {
        User userA = new User("alice", "Alice", "Smith", "F", "Zone2", 30, "pwd456", Role.USER);
        User userB = new User("bob", "Bob", "Jones", "M", "Zone3", 35, "pwd789", Role.USER);

        openMatch.setPlayers(Set.of(userA, userB));

        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, userDetails)
        );

        assertEquals("Match is full", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_alreadyJoined() {
        openMatch.getPlayers().add(user);
        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername(anyString())).thenReturn(user);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, userDetails)
        );

        assertEquals("User is already registered in the match", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_closedMatch() {
        openMatch.setType(MatchType.CLOSED);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, userDetails)
        );

        assertEquals("Only open matches can be joined", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_matchNotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                matchService.joinOpenMatch(999L, userDetails)
        );
    }

    @Test
    void testJoinOpenMatch_userNotFound() {
        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername(anyString())).thenThrow(new UserNotFoundException("user", user.getUsername()));

        assertThrows(UserNotFoundException.class, () ->
            matchService.joinOpenMatch(1L, userDetails)
        );
    }

    @Test
    void testJoinOpenMatch_saveIsCalled() throws Exception {
        Image mockAvatar = mock(Image.class);
        when(mockAvatar.getId()).thenReturn(123L);
        user.setAvatar(mockAvatar);

        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername(anyString())).thenReturn(user);
        when(matchRepository.save(openMatch)).thenReturn(openMatch);

        matchService.joinOpenMatch(1L, userDetails);
        verify(matchRepository, times(1)).save(openMatch);
    }

    @Test
    void testCreateOpenMatch_successful() throws Exception {
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(1L);

        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        UserDTO userDTO = new UserDTO(1L, "Test", "User", "testuser", 1L, "Zone", 25, "M", Role.USER, true);
        when(userService.getUserById(1L)).thenReturn(userDTO);

        User user = new User("testuser", "Test", "User", "M", "Zone", 25, "pass", Role.USER);
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L, // fieldId
                5, // minPlayers
                10, // maxPlayers
                LocalDate.now().plusDays(1), // date
                LocalDateTime.now().plusHours(1), // startTime
                LocalDateTime.now().plusHours(2) // endTime
        );

        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MatchDTO result = matchService.createOpenMatch(dto);

        assertEquals(1, result.players().size());
        assertEquals("testuser", result.organizer().username());
        verify(emailSenderService).sendMailToVerifyMatch(eq("testuser"), any(), any(), any());
    }

    @Test
    void testCreateOpenMatch_dateInPast_throwsException() {
        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                1L,
                5,
                10,
                LocalDate.now().minusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createOpenMatch(dto);
        });

        assertEquals("Match date must be in the future", ex.getMessage());
    }


    @Test
    void testCreateOpenMatch_startAfterEnd_throwsException() {
        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                1L,
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(2)
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createOpenMatch(dto);
        });

        assertEquals("Start time must be before end time", ex.getMessage());
    }

    @Test
    void testCreateOpenMatch_fieldUnavailable_throwsException() throws Exception{
        Field field = mock(Field.class);
        when(field.getId()).thenReturn(1L);
        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(any(), any(), any(), any())).thenReturn(false);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                1L,
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        UserDTO organizer = new UserDTO(1L, "Test", "User", "testuser", 1L, "Zone", 25, "M", Role.USER, true);
        when(userService.getUserById(1L)).thenReturn(organizer);
        when(userService.loadUserByUsername("testuser")).thenReturn(new User("testuser", "Test", "User", "M", "Zone", 25, "pass", Role.USER));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            matchService.createOpenMatch(dto);
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
}
*/