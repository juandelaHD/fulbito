package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.fields.FieldService;
import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.user.Role;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceClosedMatchTest {

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserService userService;

    @Mock
    private FieldService fieldService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private MatchInvitationService matchInvitationService;

    @Mock
    private Match openMatch;

    @Mock
    private User user;

    @Mock
    private AvatarImage avatarImage;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        Field field = mock(Field.class);
        User organizer = new User("organizer", "Org", "User", "M", "Zone", 30, "pass", Role.USER);
        openMatch = new Match(field, organizer, MatchStatus.PENDING, MatchType.OPEN,
                1,
                2,
                LocalDate.now().plusDays(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3)
        );
        user = new User("testuser", "Test", "User", "M", "Zone1", 25, "pass123", Role.USER);
        user.setAvatar(avatarImage);
        AvatarImage avatar = mock(AvatarImage.class);
        user.setAvatar(avatar);
        openMatch.getOrganizer().setAvatar(avatar);
    }
    
}


