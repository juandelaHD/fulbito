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
    private Field field;
    private LocalDate date;
    private LocalTime hour;
    private int maxPlayers;
    private boolean close;

    @ManyToMany
    @JoinTable(
        name = "match_users",
        joinColumns = @JoinColumn(name = "match_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>();

    public int getMissingPlayers() {
        return maxPlayers - players.size();
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public boolean started() {
        return LocalDate.now().isAfter(date)
                || (LocalDate.now().isEqual(date) && LocalTime.now().isAfter(hour));
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

    public Set<User> getPlayers() {
        return players;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Long getId() {
        return id;
    }

    public void setClose(boolean close) {
        this.close = close;
    } 

    public Boolean getClose() {
        return close;
    } 
}
