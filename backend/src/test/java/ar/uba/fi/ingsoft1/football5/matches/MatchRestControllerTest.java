package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MatchRestController.class)
@ContextConfiguration(classes = MatchRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @Mock
    private UserService userService;

    @Mock
    private FieldService fieldService;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private EmailSenderService emailSenderService;

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

        Mockito.when(matchService.joinOpenMatch(1L, mock(JwtUserDetails.class))).thenReturn(fakeMatch);

        mockMvc.perform(post("/matches/1/join")
                        .param("userId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.matchType").value("OPEN"))
                .andExpect(jsonPath("$.organizer.username").value("testuser"));
    }
}
