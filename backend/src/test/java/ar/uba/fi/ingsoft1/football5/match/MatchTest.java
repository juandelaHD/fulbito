package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchTest {

    @Test
    void testEstaLleno_TrueCuandoSeLleno() {
        Match match = new Match();
        match.setMaxJugadores(1);
        match.getJugadores().add(crearUsuarioDePrueba());
        mockCancha(match);

        assertTrue(match.estaLleno());
    }

    @Test
    void testYaEmpezo_TrueSiEsDeAyer() {
        Match match = new Match();
        match.setFecha(LocalDate.now().minusDays(1));
        match.setHora(LocalTime.now());
        mockCancha(match);

        assertTrue(match.yaEmpezo());
    }

    @Test
    void testJugadoresFaltantes() {
        Match match = new Match();
        match.setMaxJugadores(5);
        match.getJugadores().add(crearUsuarioDePrueba());
        mockCancha(match);

        assertEquals(4, match.getJugadoresFaltantes());
    }

    private User crearUsuarioDePrueba() {
        return new User(
            "juanperez",         // username
            "Juan",              // firstName
            "Pérez",             // lastName
            "Masculino",         // gender
            "Centro",            // zone
            30,                  // age
            "password123",       // password
            Role.USER          // role
        );
    }

    private void mockCancha(Match match) {
        Field canchaMock = mock(Field.class);
        when(canchaMock.getName()).thenReturn("Cancha Test");
        match.setCancha(canchaMock);
    }
}

