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

    private Field crearCancha(String nombre) {
        Field field = mock(Field.class);
        when(field.getName()).thenReturn(nombre);
        return field;
    }

    @Test
    void testInscribirse_Exitoso() {
        Match match = crearPartido(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = crearUsuario(1L);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(matchRepo.save(any())).thenReturn(match);

        InscripcionResponse response = matchService.inscribirse(1L, 1L);

        assertEquals("Inscripción exitosa", response.getMensaje());
        assertTrue(match.getJugadores().contains(user));
    }

    @Test
    void testInscribirse_UsuarioYaInscrito() {
        Match match = crearPartido(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = crearUsuario(1L);
        match.getJugadores().add(user);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.inscribirse(1L, 1L));
        assertEquals("Ya estás inscrito", ex.getMessage());
    }

    @Test
    void testInscribirse_PartidoLleno() {
        Match match = crearPartido(1, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        User user = crearUsuario(1L);
        match.getJugadores().add(crearUsuario(2L));

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.inscribirse(1L, 1L));
        assertEquals("El partido ya está lleno", ex.getMessage());
    }

    @Test
    void testInscribirse_PartidoYaEmpezo() {
        Match match = crearPartido(5, false, LocalDate.now().minusDays(1), LocalTime.of(15, 0));
        User user = crearUsuario(1L);

        when(matchRepo.findById(1L)).thenReturn(Optional.of(match));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> matchService.inscribirse(1L, 1L));
        assertEquals("El partido ya comenzó", ex.getMessage());
    }

    @Test
    void testObtenerPartidosDisponibles_FiltraCorrectamente() {
        Match partido1 = crearPartido(5, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0));
        Match partido2 = crearPartido(5, false, LocalDate.now().minusDays(1), LocalTime.of(15, 0)); // ya pasó
        Match partido3 = crearPartido(1, false, LocalDate.now().plusDays(1), LocalTime.of(15, 0)); // lleno
        partido3.getJugadores().add(crearUsuario(2L));

        when(matchRepo.findByCerradoFalse()).thenReturn(List.of(partido1, partido2, partido3));

        List<MatchSummaryDTO> disponibles = matchService.obtenerPartidosDisponibles();

        assertEquals(1, disponibles.size());
        assertEquals("Cancha Test", disponibles.get(0).getCancha());
    }

    private Match crearPartido(int maxJugadores, boolean cerrado, LocalDate fecha, LocalTime hora) {
        Match match = new Match();
        match.setMaxJugadores(maxJugadores);
        match.setCerrado(cerrado);
        match.setFecha(fecha);
        match.setHora(hora);
        match.setCancha(crearCancha("Cancha Test")); 
        return match;
    }

    private User crearUsuario(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }
}
