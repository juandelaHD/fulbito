package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitation;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.PasswordResetService;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.PasswordResetToken;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.PasswordResetTokenRepository;
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
    private final PasswordResetService passwordResetService;
    private final MatchRepository matchRepository;
    private final MatchInvitationService matchInvitationService;

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            ImageService imageService,
            EmailSenderService emailService,
            PasswordResetService passwordResetService,
            MatchRepository matchRepository,
            MatchInvitationService matchInvitationService
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.imageService = imageService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.matchRepository = matchRepository;
        this.matchInvitationService = matchInvitationService;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND, username));
    }

    public UserDTO getUser(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return new UserDTO(user);
    }

    Optional<TokenDTO> createUser(UserCreateDTO data, MultipartFile avatar) throws IOException {

        if (userRepository.findByUsername(data.username().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        var user = data.asUser(passwordEncoder::encode);
        user.setEmailConfirmed(false);

        if (data.invitationToken() != null) {
            var invitation = matchInvitationService.validateToken(data.invitationToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token or expired"));
            user.setInvitationToken(data.invitationToken());
        }

        User savedUser = userRepository.save(user);
        imageService.saveImage(savedUser, avatar);

        String token = UUID.randomUUID().toString();
        user.setEmailConfirmationToken(token);

        userRepository.save(user);

        emailService.sendMailToVerifyAccount(user.getUsername(), token);
        return Optional.of(generateTokens(user));
    }

    Optional<TokenDTO> loginUser(UserCredentials data) {
        Optional<User> maybeUser = userRepository.findByUsername(data.getUsername().toLowerCase());
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

        if (user.getInvitationToken() != null) {
            matchInvitationService.validateToken(user.getInvitationToken()).ifPresent(inv -> {
                matchInvitationService.markAsUsed(inv, user);
                var match = inv.getMatch();
                match.addPlayer(user);
                matchRepository.save(match);
            });
            user.setInvitationToken(null);
        }

        userRepository.save(user);
        return Optional.of(user);
    }

    public void initiatePasswordReset(String email) {
        userRepository.findByUsername(email.toLowerCase()).ifPresent(user -> {
            PasswordResetToken token = passwordResetService.createToken(user);
            emailService.sendPasswordResetMail(user.getUsername(), token.getToken());
        });
        // Always return success to avoid user enumeration
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // Validate token and reset password
        User user = passwordResetService.validateToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetService.invalidateToken(token);
        emailService.sendPasswordChangedMail(user.getUsername());
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
