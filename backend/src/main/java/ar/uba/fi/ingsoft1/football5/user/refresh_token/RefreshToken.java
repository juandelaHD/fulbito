package ar.uba.fi.ingsoft1.football5.user.refresh_token;

import ar.uba.fi.ingsoft1.football5.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class RefreshToken {
    @Id
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    public RefreshToken() {}

    public RefreshToken(String content, User user, Instant expiresAt) {
        this.content = content;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public String value() {
        return this.content;
    }

    public User user() {
        return this.user;
    }

    public boolean isValid() {
        return expiresAt.isAfter(Instant.now());
    }
}
