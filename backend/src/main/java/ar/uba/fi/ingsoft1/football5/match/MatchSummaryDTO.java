package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.FieldDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchSummaryDTO {

    @Schema(description = "Match's Id", example = "1231")
    private Long id;

    @Schema(description = "Field information")
    private FieldDTO field;

    @Schema(description = "Date of the match", example = "1-1-2026")
    private LocalDate date;

    @Schema(description = "The hour when the match begins", example = "8:30 pm")
    private LocalTime hour;

    @Schema(description = "How much players more need the match to be full", example = "3")
    private int missingPlayers;

    public MatchSummaryDTO(Long id, FieldDTO field, LocalDate date, LocalTime hour, int missingPlayers) {
        this.id = id;
        this.field = field;
        this.date = date;
        this.hour = hour;
        this.missingPlayers = missingPlayers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FieldDTO getField() {
        return field;
    }

    public void setField(FieldDTO field) {
        this.field = field;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        this.hour = hour;
    }

    public int getMissingPlayerss() {
        return missingPlayers;
    }

    public void setMissingPlayers(int missingPlayers) {
        this.missingPlayers = missingPlayers;
    }
}
