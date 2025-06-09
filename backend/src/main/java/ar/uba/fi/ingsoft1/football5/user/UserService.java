package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.teams.TeamDTO;
import ar.uba.fi.ingsoft1.football5.user.email.EmailSenderService;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.PasswordResetService;
import ar.uba.fi.ingsoft1.football5.user.password_reset_token.PasswordResetToken;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshToken;
import ar.uba.fi.ingsoft1.football5.user.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            ImageService imageService,
            EmailSenderService emailService,
            PasswordResetService passwordResetService
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.imageService = imageService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND, username));
    }

    public Optional<UserDTO> getUserByUsername(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return Optional.of(new UserDTO(user));
    }

    public Optional<UserDTO> getUserByDetails(JwtUserDetails userDetails) {
        return userRepository.findByUsername(userDetails.username())
                .map(UserDTO::new);
    }

    Optional<TokenDTO> createUser(UserCreateDTO data, MultipartFile avatar) throws IOException {

        if (userRepository.findByUsername(data.username().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        var user = data.asUser(passwordEncoder::encode);
        user.setEmailConfirmed(false);

        User savedUser = userRepository.save(user);
        imageService.saveAvatarImage(savedUser, avatar);

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

    public Optional<List<TeamDTO>> getTeamsByUsername(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        List<TeamDTO> teams = user.getTeams().stream()
                .map(TeamDTO::new)
                .toList();
        return Optional.of(teams);
    }

    public List<MatchHistoryDTO> getPlayedMatches(JwtUserDetails userDetails) throws UserNotFoundException{
        User user = loadUserByUsername(userDetails.username());
        List<MatchHistoryDTO> playedMatches = new ArrayList<>();
        for (Match match : user.getJoinedMatches()) {
            playedMatches.add(new MatchHistoryDTO(match));
        }
        return playedMatches;
    }

    public List<MatchHistoryDTO> getReservationsByUser(JwtUserDetails userDetails) throws UserNotFoundException {
        User user = loadUserByUsername(userDetails.username());
        List<MatchHistoryDTO> reservations = new ArrayList<>();
        for (Match match : user.getOrganizedMatches()) {
            reservations.add(new MatchHistoryDTO(match));
        }
        return reservations;
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

    public void initiatePasswordReset(String email) {
        userRepository.findByUsername(email.toLowerCase()).ifPresent(user -> {
            PasswordResetToken token = passwordResetService.createToken(user);
            emailService.sendPasswordResetMail(user.getUsername(), token.getToken());
        });
        // Always return success to avoid user enumeration
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        // Validate token and reset password
        User user = passwordResetService.validateToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));
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
