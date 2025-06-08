package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = MatchRestController.class)
class MatchRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("deprecation")
    @MockBean
    private MatchService matchService;

    @SuppressWarnings("deprecation")
    @MockBean
    private JwtService jwtService;

    private JwtUserDetails userDetails;
    private UserDTO organizer;
    private MatchDTO match;

    @BeforeEach
    void setUp(){
        userDetails = new JwtUserDetails("testUser", "USER");

        organizer = new UserDTO(
                        1L, "Test", "User", "testuser",
                        "/images/1", "Zone", 25, "M",
                        Role.ADMIN, true);

        match = new MatchDTO(
                1L,
                null,
                organizer,
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
    }

    @Test
    void testJoinOpenMatchEndpoint_returnsMatchDTO() throws Exception {

        Mockito.when(matchService.joinOpenMatch(eq(1l), any(JwtUserDetails.class))).thenReturn(match);

        JwtUserDetails userDetails = new JwtUserDetails("testuser", "USER");

        mockMvc.perform(post("/matches/1/join")
                    .with(authentication(new TestingAuthenticationToken(userDetails,null,"USER")))
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.matchType").value("OPEN"))
                .andExpect(jsonPath("$.organizer.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void testGetMatchById_NotFound() throws Exception {
        Long nonExistentId = 999l;
        when(matchService.getMatchById(nonExistentId))
        .thenThrow(new ItemNotFoundException("Match not found", nonExistentId));

        mockMvc.perform(get("/matches/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
     
    @Test
    @WithMockUser
    void testCreateOpenMatch_withInvalidData_shouldFail() throws Exception {
        MatchCreateDTO invalidDto = new MatchCreateDTO(
                                null,
                                null,
                                null,
                                null,
                                LocalDate.of(2025,6,15),
                                LocalDateTime.now().plusHours(1),
                                LocalDateTime.now().plusHours(2)
        );
        mockMvc.perform(post("/matches/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetAvailableOpenMatches_returnsListOfMatches() throws Exception {

        MatchDTO match2 = new MatchDTO(
                2L,
                null,
                organizer,
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

        List<MatchDTO> matches = List.of(match, match2);

        Mockito.when(matchService.getAvailableOpenMatches()).thenReturn(matches);

        mockMvc.perform(get("/matches/open-available")
                .with(authentication(new TestingAuthenticationToken(userDetails, null, "USER")))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }
}