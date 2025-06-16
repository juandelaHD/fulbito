package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.fields.schedules.ScheduleService;
import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
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
class MatchServiceOpenMatchTest {

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
    private MatchInvitationService matchInvitationService;

    @Mock
    private AvatarImage avatarImage;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private MatchService matchService;

    private Match openMatch;
    private User organizer;
    private User user;

    @BeforeEach
    void setUp() {
        Field field = mock(Field.class);
        organizer = new User("organizer", "Org", "User", "M", "Zone", 30, "pass", Role.USER);
        openMatch = new Match(field, organizer, MatchStatus.PENDING, MatchType.OPEN,
                1,
                2,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
        user = new User("testuser", "Test", "User", "M", "Zone1", 25, "pass123", Role.USER);

        AvatarImage avatar = mock(AvatarImage.class);
        user.setAvatar(avatar);
        openMatch.getOrganizer().setAvatar(avatar);
    }
 
    @Test
    void testJoinOpenMatch_successful() throws Exception {
        AvatarImage mockAvatar = mock(AvatarImage.class);
        User organizer = mock(User.class);
        when(mockAvatar.getId()).thenReturn(123L);
        when(organizer.getAvatar()).thenReturn(mockAvatar);
        openMatch.setOrganizer(organizer);

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
        AvatarImage mockAvatar = mock(AvatarImage.class);
        User organizer = mock(User.class);
        when(mockAvatar.getId()).thenReturn(123L);
        when(organizer.getAvatar()).thenReturn(mockAvatar);
        openMatch.setOrganizer(organizer);

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
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(anyLong(), any(), any(), any())).thenReturn(true);

        when(userDetails.username()).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                null,
                null,
                5,
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
 
    @Test
    void testCreateOpenMatch_dateInPast_throwsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new MatchCreateDTO(
                    MatchType.OPEN,
                    1L,
                    null,
                    null,
                    5,
                    10,
                    LocalDate.now().minusDays(3),
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(2)

            );
        });

        assertEquals("Match date must be in the future", ex.getMessage());
    }

    @Test
    void testCreateOpenMatch_startAfterEnd_throwsException() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new MatchCreateDTO(
                    MatchType.OPEN,
                    1L,
                    null,
                    null,
                    5,
                    10,
                    LocalDate.now().plusDays(1),
                    LocalDateTime.now().plusHours(3), 
                    LocalDateTime.now().plusHours(2)
            );
        });

        assertEquals("Start time must be before end time", ex.getMessage());
    }

    @Test
    void testCreateOpenMatch_fieldIsDisabled_throwsException() throws Exception{
        Field field = mock(Field.class);
        when(field.isEnabled()).thenReturn(false);

        when(fieldService.loadFieldById(1L)).thenReturn(field);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                null,
                null,
                5,
                10,
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
    void testCreateOpenMatch_fieldUnavailableForDateAndTime_throwsException() throws Exception{
        Field field = mock(Field.class);
        when(field.isEnabled()).thenReturn(true);

        when(fieldService.loadFieldById(1L)).thenReturn(field);
        when(fieldService.validateFieldAvailability(any(), any(), any(), any())).thenReturn(false);

        MatchCreateDTO dto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                null,
                null,
                5,
                10,
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
    void testLeaveOpenMatch_asPlayer_successful() throws Exception {
        openMatch.addPlayer(user);
        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        matchService.leaveOpenMatch(1L, userDetails);

        assertFalse(openMatch.getPlayers().contains(user));
        verify(matchRepository).save(openMatch);
        verify(emailSenderService).sendUnsubscribeMail(eq("testuser"), any(), any(), any());
    }

    @Test
    void testLeaveOpenMatch_notPlayer_throwsException() {
        when(userDetails.username()).thenReturn("testuser");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername("testuser")).thenReturn(user);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.leaveOpenMatch(1L, userDetails));

        assertEquals("You are not registered in this match.", ex.getMessage());
    }

    @Test
    void testLeaveOpenMatch_unmodifiableState_throwsException() {
        for (MatchStatus status : List.of(MatchStatus.SCHEDULED, MatchStatus.IN_PROGRESS, MatchStatus.FINISHED, MatchStatus.CANCELLED)) {
            openMatch.setStatus(status);
            when(userDetails.username()).thenReturn("testuser");
            when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
            when(userService.loadUserByUsername("testuser")).thenReturn(user);

            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    matchService.leaveOpenMatch(1L, userDetails));

            assertEquals("Cannot leave a match that is already " + status + ".", ex.getMessage());
        }
    }

    @Test
    void testLeaveOpenMatch_asOrganizer_cancelsMatch() throws Exception {
        openMatch.addPlayer(user);
        when(userDetails.username()).thenReturn("organizer");
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserByUsername("organizer")).thenReturn(openMatch.getOrganizer());

        matchService.leaveOpenMatch(1L, userDetails);

        assertEquals(MatchStatus.CANCELLED, openMatch.getStatus());
        verify(emailSenderService).sendMatchCancelledMail(eq("testuser"), any(), any(), any());
        verify(emailSenderService).sendMatchCancelledMail(eq("organizer"), any(), any(), any());
    }

    @Test
    void testLeaveOpenMatch_matchNotFound() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () ->
                matchService.leaveOpenMatch(99L, userDetails));
    }

    @Test
    void testLeaveOpenMatch_userNotFound() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userDetails.username()).thenReturn("testuser");
        when(userService.loadUserByUsername("testuser")).thenThrow(new UserNotFoundException("user", "testuser"));

        assertThrows(UserNotFoundException.class, () ->
                matchService.leaveOpenMatch(1L, userDetails));
    }

    @Test
    void testGetAvailableOpenMatches_filtersCorrectly() {
        openMatch.setStatus(MatchStatus.SCHEDULED);
        openMatch.setStartTime(LocalDateTime.now().plusHours(1));
        openMatch.setPlayers(Set.of());
        when(matchRepository.findByTypeAndStatusInAndStartTimeAfterAndPlayers_SizeLessThan(anyList(), any()))
                .thenReturn(List.of(openMatch));

        List<MatchDTO> result = matchService.getAvailableOpenMatches();
        assertEquals(1, result.size());
    }

    @Test
    void testGetMatchById_notFound_shouldThrowException(){
        Long matchId = 1l;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, ()-> matchService.getMatchById(matchId));
        verify(matchRepository).findById(matchId);
    }

    @Test
    void startMatch_ShouldThrowException_WhenMatchNotFound() {
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> matchService.startMatch(matchId, userDetails));
        verify(matchRepository).findById(matchId);
    }

    @Test
    void finishMatch_ShouldThrowException_WhenMatchNotFound() {
        Long matchId = 1L;
        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> matchService.finishMatch(matchId, userDetails));
        verify(matchRepository).findById(matchId);
    }

}
