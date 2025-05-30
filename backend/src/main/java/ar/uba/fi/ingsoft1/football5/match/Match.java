package ar.uba.fi.ingsoft1.football5.match;

import ar.uba.fi.ingsoft1.football5.user.User;
import ar.uba.fi.ingsoft1.football5.fields.Field;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Match {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field cancha;
    private LocalDate fecha;
    private LocalTime hora;
    private int maxJugadores;
    private boolean cerrado;

    @ManyToMany
    @JoinTable(
        name = "match_users",
        joinColumns = @JoinColumn(name = "match_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> jugadores = new HashSet<>();

    public int getJugadoresFaltantes() {
        return maxJugadores - jugadores.size();
    }

    public boolean estaLleno() {
        return jugadores.size() >= maxJugadores;
    }

    public boolean yaEmpezo() {
        return LocalDate.now().isAfter(fecha)
                || (LocalDate.now().isEqual(fecha) && LocalTime.now().isAfter(hora));
    }

    public Field getCancha() {
        return cancha;
    }

    public void setCancha(Field cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Set<User> getJugadores() {
        return jugadores;
    }

    public Long getId() {
        return id;
    }

    public void setMaxJugadores(int maxJugadores) {
    this.maxJugadores = maxJugadores;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setCerrado(boolean cerrado) {
        this.cerrado = cerrado;
    }

    
}
