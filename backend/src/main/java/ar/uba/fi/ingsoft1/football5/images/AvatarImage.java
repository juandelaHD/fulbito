package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.user.UserRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("AVATAR")
public class AvatarImage extends Image {

    @JsonBackReference("user-image")
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Transient
    private static UserRepository userRepository;

    public static void injectRepository(UserRepository repository) {
        userRepository = repository;
    }

    @Override
    public void validateOwnership(JwtUserDetails userDetails) {
        String username = userDetails.username();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user", username));

        if (!user.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
            throw new IllegalArgumentException("You do not own this avatar image.");
        }
    }

    protected AvatarImage() {}

    public AvatarImage(byte[] data, User user) {
        super(data);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
