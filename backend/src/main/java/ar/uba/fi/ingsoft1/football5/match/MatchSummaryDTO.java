package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import java.time.LocalDate;
import java.time.LocalTime;

public class MatchSummaryDTO {
    private Long id;
    private Field field;
    private LocalDate date;
    private LocalTime hour;
    private int missingPlayers;

    public MatchSummaryDTO(Long id, Field field, LocalDate date, LocalTime hour, int missingPlayers) {
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

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
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
