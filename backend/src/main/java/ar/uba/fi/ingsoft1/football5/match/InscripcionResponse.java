package ar.uba.fi.ingsoft1.football5.match;

import java.time.LocalDate;
import java.time.LocalTime;

public class InscripcionResponse {
    private String mensaje;
    private Long matchId;
    private String cancha;
    private LocalDate fecha;
    private LocalTime hora;

    public InscripcionResponse(String mensaje, Match match) {
        this.mensaje = mensaje;
        this.matchId = match.getId();
        this.cancha =  match.getCancha().getName();
        this.fecha = match.getFecha();
        this.hora = match.getHora();
    }

     public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getCancha() {
        return cancha;
    }

    public void setCancha(String cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}
