package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarImageServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AvatarImageService avatarImageService;

    @Mock
    private JwtUserDetails userDetails;

    @Mock
    private User user;

    @Mock
    private User anotherUser;

    @Mock
    private Image image;

    @Test
    void validateAvatarOwnership_whenUserNotFound_ThrowsUserNotFoundException() {
        when(userDetails.username()).thenReturn("test-user");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            avatarImageService.validateAvatarOwnership(image, userDetails);
        });

        assertEquals("Failed to find user with username test-user", exception.getMessage());
    }

    @Test
    void validateAvatarOwnership_whenUserIsNotOwner_DoesNotThrowException() {
        when(userDetails.username()).thenReturn("test-user");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        when(image.getUser()).thenReturn(anotherUser);
        when(anotherUser.getUsername()).thenReturn("another-user");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            avatarImageService.validateAvatarOwnership(image, userDetails);
        });

        assertEquals("You do not own this avatar image.", exception.getMessage());
    }

    @Test
    void validateAvatarOwnership_whenUserOwnsAvatarImage_DoesNotThrowException() {
        when(userDetails.username()).thenReturn("test-user");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        when(image.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("test-user");

        assertDoesNotThrow(() -> avatarImageService.validateAvatarOwnership(image, userDetails));
    }
}
