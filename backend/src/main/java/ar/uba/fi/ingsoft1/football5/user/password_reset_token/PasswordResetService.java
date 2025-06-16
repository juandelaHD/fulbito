package ar.uba.fi.ingsoft1.football5.user.password_reset_token;

import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepo;

    public PasswordResetService(PasswordResetTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Transactional
    public PasswordResetToken createToken(User user) {
        tokenRepo.deleteByUserId(user.getId());
        tokenRepo.flush();
        PasswordResetToken token = new PasswordResetToken(UUID.randomUUID().toString(), user, LocalDateTime.now().plusHours(1));
        return tokenRepo.save(token);
    }

    public Optional<User> validateToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(PasswordResetToken::getUser);
    }

    public void invalidateToken(String token) {
        tokenRepo.findByToken(token).ifPresent(tokenRepo::delete);
    }
}