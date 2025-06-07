package ar.uba.fi.ingsoft1.football5.matches.invitation;

import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import ar.uba.fi.ingsoft1.football5.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MatchInvitationService {
    private final MatchInvitationRepository invitationRepository;
    private final MatchRepository matchRepository;

    public MatchInvitationService(MatchInvitationRepository invitationRepository, MatchRepository matchRepository) {
        this.invitationRepository = invitationRepository;
        this.matchRepository = matchRepository;
    }

    public MatchInvitation createInvitation(Long matchId, int hoursValid) {
        Match match = matchRepository.findById(matchId).orElseThrow();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(hoursValid);
        MatchInvitation invitation = new MatchInvitation(token, match, expiry);
        return invitationRepository.save(invitation);
    }

    public Optional<MatchInvitation> validateToken(String token) {
        return invitationRepository.findByToken(token)
                .filter(inv -> !inv.isUsed() && inv.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    public void markAsUsed(MatchInvitation invitation, User user) {
        invitation.setUsed(true);
        invitation.setInvitedUser(user);
        invitationRepository.save(invitation);
    }
}