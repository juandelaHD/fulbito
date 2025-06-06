package ar.uba.fi.ingsoft1.football5.user.password_reset_token;

import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}