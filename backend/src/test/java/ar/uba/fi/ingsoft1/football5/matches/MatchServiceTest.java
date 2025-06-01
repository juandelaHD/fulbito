package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.images.Image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchServiceTest {

    private MatchRepository matchRepository;
    private UserService userService;
    private FieldService fieldService;
    private MatchService matchService;

    private Match openMatch;
    private User user;

    @BeforeEach
    void setUp() {
        matchRepository = mock(MatchRepository.class);
        userService = mock(UserService.class);
        fieldService = mock(FieldService.class);
        

        matchService = new MatchService(matchRepository, userService, fieldService);

        // Match Setup
        Field field = mock(Field.class);
        User organizer = mock(User.class);
        openMatch = new Match(field, organizer, MatchStatus.SCHEDULED, MatchType.OPEN,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
        openMatch.setMaxPlayers(2);
        openMatch.setMinPlayers(1);

        // User Setup
        user = new User("testuser", "Test", "User", "M", "Zone1", 25, "pass123", Role.USER);

    }

    @Test
    void testJoinOpenMatch_successful() throws Exception {
        Image mockAvatar = mock(Image.class);
        when(mockAvatar.getId()).thenReturn(123L); 
        user.setAvatar(mockAvatar);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserById(99L)).thenReturn(user);
        when(matchRepository.save(openMatch)).thenReturn(openMatch);

        MatchDTO result = matchService.joinOpenMatch(1L, 99L);

        assertEquals(1, result.players().size());
        assertTrue(openMatch.getPlayers().contains(user));
    }

    @Test
    void testJoinOpenMatch_alreadyStarted() {
        openMatch.setStartTime(LocalDateTime.now().minusMinutes(10));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, 99L)
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
                matchService.joinOpenMatch(1L, 99L)
        );

        assertEquals("Match is full", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_alreadyJoined() {
        openMatch.getPlayers().add(user);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserById(99L)).thenReturn(user);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, 99L)
        );

        assertEquals("User is already registered in the match", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_closedMatch() {
        openMatch.setType(MatchType.CLOSED);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                matchService.joinOpenMatch(1L, 99L)
        );

        assertEquals("Only open matches can be joined", ex.getMessage());
    }

    @Test
    void testJoinOpenMatch_matchNotFound() {
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                matchService.joinOpenMatch(999L, 99L)
        );
    }

    @Test
    void testJoinOpenMatch_userNotFound() {
        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserById(99L)).thenThrow(new UserNotFoundException("user", user.getUsername()));

        assertThrows(UserNotFoundException.class, () ->
            matchService.joinOpenMatch(1L, 99L)
        );
    }

    @Test
    void testJoinOpenMatch_saveIsCalled() throws Exception {
        Image mockAvatar = mock(Image.class);
        when(mockAvatar.getId()).thenReturn(123L);
        user.setAvatar(mockAvatar);

        when(matchRepository.findById(1L)).thenReturn(Optional.of(openMatch));
        when(userService.loadUserById(99L)).thenReturn(user);
        when(matchRepository.save(openMatch)).thenReturn(openMatch);

        matchService.joinOpenMatch(1L, 99L);
        verify(matchRepository, times(1)).save(openMatch);
    }

}
