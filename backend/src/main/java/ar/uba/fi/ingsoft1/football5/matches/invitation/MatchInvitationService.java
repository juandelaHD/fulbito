package ar.uba.fi.ingsoft1.football5.matches.invitation;

import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.matches.MatchStatus;
import ar.uba.fi.ingsoft1.football5.matches.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createInvitation(Long matchId) throws IllegalArgumentException {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));
        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new IllegalArgumentException("Cannot create invitation for this match. Current status: " + match.getStatus());
        }
        if (match.getInvitation() != null && match.getInvitation().isValid()) {
            throw new IllegalArgumentException("An invitation already exists for this match.");
        }
        if (match.getPlayers().size() >= match.getMaxPlayers()) {
            throw new IllegalArgumentException("Cannot create invitation for a match that is already full.");
        }
        if (match.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot create invitation for a match that has already started.");
        }
        String token = UUID.randomUUID().toString();
        MatchInvitation invitation = new MatchInvitation(token, match);
        invitation = invitationRepository.save(invitation);

        match.setInvitation(invitation);
        matchRepository.save(match);

        new MatchInvitationDTO(invitation);
    }

    public Optional<MatchInvitation> validateToken(String token) throws IllegalArgumentException {
        MatchInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token: " + token));
        if (!invitation.isValid()) {
            throw new IllegalArgumentException("Invitation token is no longer valid: " + token);
        }
        return Optional.of(invitation);
    }

    public void invalidateMatchInvitation(Match match) throws IllegalArgumentException {
        Optional<MatchInvitation> invitation = invitationRepository.findByToken(match.getInvitation().getToken());
        if (invitation.isPresent()) {
            MatchInvitation inv = invitation.get();
            if (inv.getMatch().getId().equals(match.getId())) {
                markAsInvalid(inv);
            } else {
                throw new IllegalArgumentException("Invitation does not match the provided match ID.");
            }
        }
    }

    public void markAsInvalid(MatchInvitation invitation) {
        invitation.setValid(false);
        invitationRepository.save(invitation);
    }

}