package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshToken;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
class UserService implements UserDetailsService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    // private final EmailVerificationTokenRepository emailTokenRepo, TODO: Uncomment when email service is implemented
    // private final EmailService emailService // TODO: Uncomment when email service is implemented

    private static final String USER_NOT_FOUND = "user";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService
            // EmailVerificationTokenRepository emailTokenRepo,  // TODO: Uncomment when email service is implemented
            // EmailService emailService  // TODO: Uncomment when email service is implemented
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        // this.emailTokenRepo = emailTokenRepo; // Uncomment when email service is implemented
        // this.emailService = emailService; // Uncomment when email service is implemented
    }

    public boolean isAdminUser(String username) {
        return userRepository.findByUsername(username)
                .map(u -> u.getRole().equals("ADMIN"))
                .orElse(false);
    }

    private String getRole(Authentication authPrincipal) {
        return authPrincipal.getAuthorities().stream()
                .filter(a -> a.getAuthority().equals(ROLE_ADMIN))
                .findFirst()
                .map(a -> ROLE_ADMIN)
                .orElse(ROLE_USER);
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND, username));
    }

    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return new UserDTO(user);
    }

    Optional<TokenDTO> createUser(UserCreateDTO data) {

        if (userRepository.findByUsername(data.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.findByEmail(data.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = data.asUser(passwordEncoder::encode);
        user.setEmailConfirmed(false);
        userRepository.save(user);

        /*
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)); // 24hs

        emailTokenRepo.save(verificationToken);

        String link = baseUrl + "/users/confirm?token=" + token;
        String emailText = "Confirmá tu cuenta haciendo clic en el siguiente enlace: " + link;

        emailService.sendEmail(user.getEmail(), "Confirmación de cuenta", emailText);
        */
        return Optional.of(generateTokens(user));
    }

    Optional<TokenDTO> loginUser(UserCredentials data) {
        Optional<User> maybeUser = userRepository.findByUsername(data.getUsername());
        return maybeUser
                .filter(user -> passwordEncoder.matches(data.getPassword(), user.getPassword()))
                .map(this::generateTokens);
    }

    Optional<TokenDTO> refresh(RefreshDTO data) {
        return refreshTokenService.findByValue(data.refreshToken())
                .map(RefreshToken::user)
                .map(this::generateTokens);
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
