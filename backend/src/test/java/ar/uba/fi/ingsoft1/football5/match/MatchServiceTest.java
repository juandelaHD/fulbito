package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    private MatchRepository matchRepo;
    private UserRepository userRepo;
    private MatchService matchService;

    @BeforeEach
    void setup() {
        matchRepo = mock(MatchRepository.class);
        userRepo = mock(UserRepository.class);
        matchService = new MatchService(matchRepo, userRepo);
    }

    private Field createField(String fieldName) {
        Field field = mock(Field.class);
        when(field.getName()).thenReturn(fieldName);
        return field;
    }

    @Test
    void successful_registration_test() {
        Match match = createMatch(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = createUser(1L);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(matchRepo.save(any())).thenReturn(match);

        InscripcionResponse response = matchService.register(1L, 1L);

        assertEquals("Inscripción exitosa", response.getMessage());
        assertTrue(match.getPlayers().contains(user));
    }

    @Test
    void test_register_user_already_registered() {
        Match match = createMatch(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = createUser(1L);
        match.getPlayers().add(user);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.register(1L, 1L));
        assertEquals("Ya estás inscrito", ex.getMessage());
    }

    @Test
    void test_signUp_fullMatch() {
        Match match = createMatch(1, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = createUser(1L);
        match.getPlayers().add(createUser(2L));

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.register(1L, 1L));
        assertEquals("El partido ya está lleno", ex.getMessage());
    }

    @Test
    void test_register_match_already_started() {
        Match match = createMatch(5, false, LocalDate.now().minusDays(1), LocalTime.of(15, 0));
        User user = createUser(1L);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.register(1L, 1L));
        assertEquals("El partido ya comenzó", ex.getMessage());
    }

    @Test
    void test_get_available_matches_with_correct_filters() {
        Match partido1 = createMatch(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        Match partido2 = createMatch(5, false, LocalDate.now().minusDays(1), LocalTime.of(15, 0)); // ya pasó
        Match partido3 = createMatch(1, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0)); // lleno
        partido3.getPlayers().add(createUser(2L));

        when(matchRepo.findByCloseFalse()).thenReturn(List.of(partido1, partido2, partido3));

        List<MatchSummaryDTO> disponibles = matchService.getAvailableMatches();

        assertEquals(1, disponibles.size());
        assertEquals("Cancha Test", disponibles.get(0).getField().getName());
    }

    private Match createMatch(int maxJugadores, boolean cerrado, LocalDate fecha, LocalTime hora) {
        Match match = new Match();
        match.setMaxPlayers(maxJugadores);
        match.setClose(cerrado);
        match.setDate(fecha);
        match.setHour(hora);
        match.setField(createField("Cancha Test")); 
        return match;
    }

    private User createUser(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }
}
