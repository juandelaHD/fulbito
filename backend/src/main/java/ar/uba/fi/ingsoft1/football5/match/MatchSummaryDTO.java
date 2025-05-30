package ar.uba.fi.ingsoft1.football5.match;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchSummaryDTO {
    private Long id;
    private String cancha;
    private LocalDate fecha;
    private LocalTime hora;
    private int jugadoresFaltantes;

    public MatchSummaryDTO(Long id, String cancha, LocalDate fecha, LocalTime hora, int jugadoresFaltantes) {
        this.id = id;
        this.cancha = cancha;
        this.fecha = fecha;
        this.hora = hora;
        this.jugadoresFaltantes = jugadoresFaltantes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getJugadoresFaltantes() {
        return jugadoresFaltantes;
    }

    public void setJugadoresFaltantes(int jugadoresFaltantes) {
        this.jugadoresFaltantes = jugadoresFaltantes;
    }
}
