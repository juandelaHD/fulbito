package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.FieldDTO;
import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationDTO;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @SuppressWarnings("deprecation")
    @MockBean
    private MatchInvitationService matchInvitationService;

    private JwtUserDetails userDetails;
    private UserDTO organizer;
    private MatchDTO match;
    private String token1 = "testToken";
    private String token2 = "testToken2";

    @BeforeEach
    void setUp(){
        userDetails = new JwtUserDetails("testUser", "USER");

        organizer = new UserDTO(
                        1L, "Test", "User", "testuser",
                        "/images/1", "Zone", 25, "M",
                        Role.ADMIN, true, true);

        match = new MatchDTO(
                1L,
                null,
                organizer,
                List.of(),
                null,
                null,
                MatchStatus.SCHEDULED,
                MatchType.OPEN,
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                false,
                new MatchInvitationDTO(token1, 1L, true),
                "1-0"
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
                                null,
                                null,
                                LocalDate.now().plusDays(1),
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
                null,
                null,
                MatchStatus.SCHEDULED,
                MatchType.OPEN,
                4,
                8,
                LocalDate.now().plusDays(2),
                LocalDateTime.now().plusHours(4),
                LocalDateTime.now().plusHours(5),
                false,
                new MatchInvitationDTO(token2, 2L, true),
                "0-1"
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

    @Test
    void testCreateOpenMatch_withValidData_shouldSucceed() throws Exception {
        MatchCreateDTO createDto = new MatchCreateDTO(
                MatchType.OPEN,
                1L,
                null,
                null,
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );

        when(matchService.createMatch(argThat(dto ->
                        dto.matchType() == MatchType.OPEN &&
                        dto.fieldId() == 1L &&
                        dto.minPlayers() == 5 &&
                        dto.maxPlayers() == 10
                ),
                any(JwtUserDetails.class)
        )).thenReturn(match);

        mockMvc.perform(post("/matches/create")
                        .with(authentication(new TestingAuthenticationToken(userDetails, null, "USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.matchType").value("OPEN"))
                .andExpect(jsonPath("$.organizer.username").value("testuser"));
    }

    @Test
    void testCreateClosedMatch_withValidTeams_shouldSucceed() throws Exception {
        // Users  
        User playerA = new User("playerA", "jorge", "A", "M", "ZoneA", 33, "pass", Role.USER);
        User playerB = new User("playerB", "juan", "B", "M", "ZoneB", 28, "pass", Role.USER);
        User playerC = new User("playerC", "agustin", "C", "Other", "ZoneC", 42, "pass", Role.USER);
        User playerD = new User("organizer", "Org", "anizer", "M", "Zone", 30, "pass", Role.USER);

        UserDTO organizer = new UserDTO(5l,"org", "anizer", "organizer", "asd", "asd", 5, "M", Role.USER, true, true);

        AvatarImage avatar = mock(AvatarImage.class);
        playerA.setAvatar(avatar);
        playerB.setAvatar(avatar);
        playerC.setAvatar(avatar);
        playerD.setAvatar(avatar);

        // Teams
        Team homeTeam = new Team("homeTeam", playerA);
        homeTeam.setId(1l);
        homeTeam.addMember(playerA);
        homeTeam.addMember(playerB);

        Team awayTeam = new Team("awayTeam", playerC);
        awayTeam.setId(2l);
        awayTeam.addMember(playerC);
        awayTeam.addMember(playerD);

        MatchCreateDTO createDto = new MatchCreateDTO(
                MatchType.CLOSED,
                1L,
                homeTeam.getId(),
                awayTeam.getId(),
                5,
                10,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );

        FieldDTO field = mock(FieldDTO.class);

        MatchDTO closedMatch = new MatchDTO(
                1L,
                field,
                organizer,
                List.of(),
                new TeamDTO(homeTeam),
                new TeamDTO(awayTeam),
                MatchStatus.SCHEDULED,
                MatchType.CLOSED,
                5,
                10,
                createDto.date(),
                createDto.startTime(),
                createDto.endTime(),
                false,
                new MatchInvitationDTO("tokenClosed", 1L, false),
                "1-1"
        );

        when(matchService.createMatch(argThat(dto ->
                        dto.matchType() == MatchType.CLOSED &&
                        dto.homeTeamId()!= null &&
                        dto.awayTeamId() != null &&
                        dto.minPlayers() == 5 &&
                        dto.maxPlayers() == 10
                ),
                any(JwtUserDetails.class)
        )).thenReturn(closedMatch);

        mockMvc.perform(post("/matches/create")
                        .with(authentication(new TestingAuthenticationToken(userDetails, null, "USER")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1l))
                .andExpect(jsonPath("$.matchType").value("CLOSED"))
                .andExpect(jsonPath("$.organizer.username").value("organizer"));
    }
}