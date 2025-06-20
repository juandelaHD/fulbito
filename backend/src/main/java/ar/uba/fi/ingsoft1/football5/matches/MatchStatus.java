package ar.uba.fi.ingsoft1.football5.matches;

public enum MatchStatus {
    // When an OPEN/CLOSED match is created.
    PENDING,

    //When the field admin accepts the OPEN match request.
    ACCEPTED,

    // When the field admin accepts the CLOSED match request or when the OPEN match teams are formed.
    SCHEDULED,

    // When the OPEN/CLOSED match is in progress (configured by the field admin).
    IN_PROGRESS,

    // When the OPEN/CLOSED match is finished (configured by the field admin).
    FINISHED,

    // When the OPEN/CLOSED match is cancelled by the field admin.
    CANCELLED
}
