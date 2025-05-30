package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import java.time.LocalDate;
import java.time.LocalTime;

public class InscripcionResponse {
    private String message;
    private Long matchId;
    private Field field;
    private LocalDate date;
    private LocalTime hour;

    public InscripcionResponse(String message, Match match) {
        this.message = message;
        this.matchId = match.getId();
        this.field =  match.getField();
        this.date = match.getDate();
        this.hour = match.getHour();
    }

     public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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
}
