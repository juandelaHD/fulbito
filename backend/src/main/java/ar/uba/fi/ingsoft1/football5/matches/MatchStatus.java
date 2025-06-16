package ar.uba.fi.ingsoft1.football5.matches;

// TODO: Check these!!!
public enum MatchStatus {
    // When a match is created, it is in the PENDING state.
    PENDING,

    // When an OPEN match is confirmed by the field admin, it is in the ACCEPTED state.
    ACCEPTED,

    // When a CLOSED match is confirmed by the field admin or when the organizer
    // form the teams, it is in the SCHEDULED state.
    SCHEDULED,

    // When a match is set as initialized by the field admin or when a player
    // tries to join a present match, it is in the IN_PROGRESS state.
    IN_PROGRESS,

    // When a match is set as finished by the field admin, it is in the FINISHED state.
    FINISHED,

    // When the organizer left the match or the field admin cancels, it is in the CANCELLED state.
    CANCELLED
}
