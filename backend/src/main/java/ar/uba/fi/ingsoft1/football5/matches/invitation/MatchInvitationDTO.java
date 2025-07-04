package ar.uba.fi.ingsoft1.football5.matches.invitation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object representing a match invitation.")
public record MatchInvitationDTO(
        @Schema(description = "Invitation token", example = "abc123-uuid")
        String token,
        @Schema(description = "Match ID", example = "42")
        Long matchId,
        @Schema(description = "Whether the invitation was used", example = "false")
        boolean valid
) {
    public MatchInvitationDTO(MatchInvitation invitation) {
        this(
                invitation.getToken(),
                invitation.getMatch().getId(),
                invitation.isValid()
        );
    }
}