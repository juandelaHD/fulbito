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
    void test_isFull() {
        Match match = new Match();
        match.setMaxPlayers(1);
        match.getPlayers().add(create_test_user());
        mockField(match);

        assertTrue(match.isFull());
    }

    @Test
    void test_isStarted() {
        Match match = new Match();
        match.setDate(LocalDate.now().minusDays(1));
        match.setHour(LocalTime.now());
        mockField(match);

        assertTrue(match.started());
    }

    @Test
    void test_missing_players() {
        Match match = new Match();
        match.setMaxPlayers(5);
        match.getPlayers().add(create_test_user());
        mockField(match);

        assertEquals(4, match.getMissingPlayers());
    }

    private User create_test_user() {
        return new User(
            "juanperez",
            "Juan",            
            "Pérez",            
            "Masculino",       
            "Centro",        
            30,      
            "password123", 
            Role.USER 
        );
    }

    private void mockField(Match match) {
        Field canchaMock = mock(Field.class);
        when(canchaMock.getName()).thenReturn("Cancha Test");
        match.setField(canchaMock);
    }
}

