package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshToken;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "user";

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final EmailSenderService emailService;

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            ImageService imageService,
            EmailSenderService emailService
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.imageService = imageService;
        this.emailService = emailService;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND, username));
    }

    public UserDTO getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(UserDTO::new)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND, id));
    }

    public UserDTO getUser(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return new UserDTO(user);
    }

    Optional<TokenDTO> createUser(UserCreateDTO data, MultipartFile avatar) throws IOException {

        if (userRepository.findByUsername(data.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        var user = data.asUser(passwordEncoder::encode);
        user.setEmailConfirmed(false);

        User savedUser = userRepository.save(user);
        imageService.saveImage(savedUser, avatar);

        String token = UUID.randomUUID().toString();
        user.setEmailConfirmationToken(token);

        userRepository.save(user);

        emailService.sendMailToVerifyAccount(user.getUsername(), token);
        return Optional.of(generateTokens(user));
    }

    Optional<TokenDTO> loginUser(UserCredentials data) {
        Optional<User> maybeUser = userRepository.findByUsername(data.getUsername());
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }

        User existingUser = maybeUser.get();

        if (!passwordEncoder.matches(data.getPassword(), existingUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        if (!existingUser.isEmailConfirmed()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email is not confirmed");
        }

        TokenDTO tokens = generateTokens(existingUser);
        return Optional.of(tokens);
    }

    Optional<TokenDTO> refresh(RefreshDTO data) {
        return refreshTokenService.findByValue(data.refreshToken())
                .map(RefreshToken::user)
                .map(this::generateTokens);
    }

    Optional<User> verifyEmail(String token) {
        Optional<User> maybeUser = userRepository.findByEmailConfirmationToken(token);
        if (maybeUser.isEmpty()) {
            return Optional.empty();
        }
        User user = maybeUser.get();
        user.setEmailConfirmed(true);
        user.setEmailConfirmationToken(null);
        userRepository.save(user);
        return Optional.of(user);
    }


    private TokenDTO generateTokens(User user) {
        String accessToken = jwtService.createToken(new JwtUserDetails(
                user.getUsername(),
                user.getRole().name()
        ));
        RefreshToken refreshToken = refreshTokenService.createFor(user);
        return new TokenDTO(accessToken, refreshToken.value());
    }
}
