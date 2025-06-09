package ar.uba.fi.ingsoft1.football5.matches.invitation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchInvitationRepository extends JpaRepository<MatchInvitation, Long> {
    Optional<MatchInvitation> findByToken(String token);
    Optional<MatchInvitation> findByMatchId(Long matchId);
}