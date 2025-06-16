package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.common.exception.UserNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtService;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.images.ImageService;
import ar.uba.fi.ingsoft1.football5.matches.MatchDTO;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitation;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitationService;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final String USER_ITEM = "user";

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final EmailSenderService emailService;
    private final PasswordResetService passwordResetService;
    private final MatchInvitationService matchInvitationService;
    private final MatchRepository matchRepository;

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            ImageService imageService,
            EmailSenderService emailService,
            PasswordResetService passwordResetService,
            MatchInvitationService matchInvitationService,
            MatchRepository matchRepository)
    {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.imageService = imageService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.matchInvitationService = matchInvitationService;
        this.matchRepository = matchRepository;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(USER_ITEM, username));
    }

    public User loadUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_ITEM, id.toString()));
    }

    public UserDTO getUserByUsername(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return new UserDTO(user);
    }

    Optional<TokenDTO> createUser(UserCreateDTO data, MultipartFile avatar) throws IOException, IllegalArgumentException {
        if (userRepository.findByUsername(data.username().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        var user = data.asUser(passwordEncoder::encode);
        user.setEmailConfirmed(false);
        boolean invitationValid = true;
        if (data.invitationToken() != null) {
            try {
                MatchInvitation invitation = matchInvitationService.validateToken(data.invitationToken())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));
                Match match = invitation.getMatch();
                if (match.getPlayers().size() >= match.getMaxPlayers()) {
                    throw new IllegalArgumentException("The match is already full");
                }
                if (match.getStatus() != MatchStatus.PENDING && match.getStatus() != MatchStatus.ACCEPTED) {
                    throw new IllegalArgumentException("The match is not open for joining");
                }
                user.setInvitationToken(data.invitationToken());
            } catch (IllegalArgumentException e) {
                invitationValid = false;
                // User can still register without an invitation
            }
        }
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

        if (!existingUser.isActiveUser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");
        }

        TokenDTO tokens = generateTokens(existingUser);
        return Optional.of(tokens);
    }

    public List<TeamDTO> getTeamsByUsername(String username) throws UserNotFoundException {
        User user = loadUserByUsername(username);
        return user.getTeams().stream()
                .map(TeamDTO::new)
                .toList();
    }

    public List<MatchDTO> getPlayedMatchesByUser(JwtUserDetails userDetails) throws UserNotFoundException{
        User user = loadUserByUsername(userDetails.username());
        return user.getJoinedMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.FINISHED)
                .map(MatchDTO::new)
                .toList();
    }

    public List<MatchDTO> getUpcomingMatchesByUser(JwtUserDetails userDetails) throws UserNotFoundException {
        User user = loadUserByUsername(userDetails.username());
        LocalDateTime now = LocalDateTime.now();
        return user.getJoinedMatches().stream()
                .filter(match -> match.getEndTime().isAfter(now) && match.getStatus() == MatchStatus.SCHEDULED)
                .map(MatchDTO::new)
                .toList();
    }

    public List<MatchDTO> getReservationsByUser(JwtUserDetails userDetails) throws UserNotFoundException {
        User user = loadUserByUsername(userDetails.username());
        return user.getOrganizedMatches().stream()
                .filter(Match::isConfirmationSent)
                .map(MatchDTO::new)
                .toList();
    }

    public List<MatchHistoryDTO> getJoinedMatchesByUser(JwtUserDetails userDetails) throws UserNotFoundException {
        User user = loadUserByUsername(userDetails.username());
        return user.getJoinedMatches().stream()
                .filter(match -> match.getStatus() == MatchStatus.ACCEPTED)
                .map(MatchHistoryDTO::new)
                .toList();
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
            try {
                MatchInvitation invitation = matchInvitationService.validateToken(user.getInvitationToken())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));
                Match match = invitation.getMatch();
                if (match.getStatus() != MatchStatus.ACCEPTED) {
                    throw new IllegalArgumentException("Cannot join match with status: " + match.getStatus());
                }
                if (match.getPlayers().size() >= match.getMaxPlayers()) {
                    throw new IllegalArgumentException("Cannot join match that is already full.");
                }
                if (match.getStartTime().isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Cannot join match that has already started.");
                }
                match.addPlayer(user);

                // Actualizar estado del partido si corresponde
                if (match.getPlayers().size() >= match.getMaxPlayers()) {
                    matchInvitationService.invalidateMatchInvitation(match);
                }
                matchRepository.save(match);
            } catch (IllegalArgumentException e) {
                // Optional?: log.warn("No se pudo unir al partido con invitación: " + e.getMessage());
            }
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
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
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
        return new TokenDTO(accessToken, refreshToken.value(), user.getRole());
    }
}
