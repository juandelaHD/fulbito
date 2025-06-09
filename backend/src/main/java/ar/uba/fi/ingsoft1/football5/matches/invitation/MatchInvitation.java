package ar.uba.fi.ingsoft1.football5.matches.invitation;

import ar.uba.fi.ingsoft1.football5.matches.Match;
import jakarta.persistence.*;

@Entity
@Table(name = "match_invitations")
public class MatchInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private boolean valid = true;

    public MatchInvitation() {}

    public MatchInvitation(String token, Match match) {
        this.token = token;
        this.match = match;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}