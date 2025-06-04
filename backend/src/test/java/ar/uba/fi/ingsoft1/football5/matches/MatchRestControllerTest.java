package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MatchRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testJoinOpenMatchEndpoint_returnsMatchDTO() throws Exception {
        MatchDTO fakeMatch = new MatchDTO(
                1L,
                null,
                new UserDTO(
                        1L, "Test", "User", "testuser",
                        1L, "Zone", 25, "M",
                        Role.USER, true),
                List.of(),
                MatchStatus.SCHEDULED,
                MatchType.OPEN,
                5,
                10,
                LocalDate.of(2025, 6, 15),
                LocalDateTime.of(2025, 6, 15, 19, 0),
                LocalDateTime.of(2025, 6, 15, 20, 0),
                false
        );

        Mockito.when(matchService.joinOpenMatch(1L, 99L)).thenReturn(fakeMatch);

        mockMvc.perform(post("/matches/1/join")
                        .param("userId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.matchType").value("OPEN"))
                .andExpect(jsonPath("$.organizer.username").value("testuser"));
    }

    @Test
    void testGetAvailableOpenMatches_returnsListOfMatches() throws Exception {
        MatchDTO match1 = new MatchDTO(
                1L,
                null,
                new UserDTO(1L, "Test", "User", "testuser", 1L, "Zone", 25, "M", Role.USER, true),
                List.of(),
                MatchStatus.SCHEDULED,
                MatchType.OPEN,
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                false
        );

        MatchDTO match2 = new MatchDTO(
                2L,
                null,
                new UserDTO(2L, "Jane", "Doe", "janedoe", 1L, "Zone", 22, "F", Role.USER, true),
                List.of(),
                MatchStatus.SCHEDULED,
                MatchType.OPEN,
                4,
                8,
                LocalDate.now().plusDays(2),
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                false
        );

        List<MatchDTO> matches = List.of(match1, match2);

        Mockito.when(matchService.getAvailableOpenMatches()).thenReturn(matches);

        mockMvc.perform(get("/matches/open-available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
