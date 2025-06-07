package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AvatarImageService {

    private final UserRepository userRepository;

    public AvatarImageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateAvatarOwnership(Image image, JwtUserDetails userDetails) {
        String username = userDetails.username();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user", username));
        User avatarOwner = image.getUser();

        if (avatarOwner != null && !avatarOwner.getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("You do not own this avatar image.");
        }
    }
}
