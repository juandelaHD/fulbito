package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MatchRestController.class)
@ContextConfiguration(classes = MatchRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private UserService userService;

    @MockBean
    private FieldService fieldService;

    @MockBean
    private MatchRepository matchRepository;

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
                        Role.USER, true, new HashSet<>(), new HashSet<>()),
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
}
