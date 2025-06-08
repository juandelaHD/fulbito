package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cancha donde se jugará el partido
    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    @JsonManagedReference("match-field")
    private Field field;

    // Organizador del partido
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference("organizer-match")
    private User organizer;

    // Jugadores inscritos para partido abierto
    @ManyToMany
    @JoinTable(
            name = "match_subscriptions",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonManagedReference("player-match")
    private Set<User> players = new HashSet<>();

    // TODO: Equipos para partido cerrado (pueden ser 2)
    // @OneToMany(mappedBy = "team_id", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Team> teams = new HashSet<>();

    // Estado del partido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.PENDING;

    // Tipo de partido (abierto o cerrado)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchType type;

    // Número mínimo de jugadores para el partido
    @Column(nullable = false)
    private Integer minPlayers;

    // Número máximo de jugadores para el partido
    @Column(nullable = false)
    private Integer maxPlayers;

    // Fecha del partido
    @Column(nullable = false)
    private LocalDate date;

    // Hora de inicio del partido
    @Column(nullable = false)
    private LocalDateTime startTime;

    // Hora de finalización del partido
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean confirmationSent = false;

    public Match() {}

    public Match(Field field, User organizer, MatchStatus status, MatchType type, Integer minPlayers, Integer maxPlayers, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        this.field = field;
        this.organizer = organizer;
        this.status = status;
        this.type = type;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Long setId(Long id) {
        this.id = id;
        return this.id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public MatchType getType() {
        return type;
    }

    public void setType(MatchType type) {
        this.type = type;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addPlayer(User player) {
        this.players.add(player);
        player.getJoinedMatches().add(this); // Assuming User has a Set<Match> matches
    }

    public void removePlayer(User player) {
        this.players.remove(player);
        player.getJoinedMatches().remove(this); // Assuming User has a Set<Match> matches
    }

    public boolean isConfirmationSent() {
        return confirmationSent;
    }

    public void setConfirmationSent(boolean confirmationSent) {
        this.confirmationSent = confirmationSent;
    }
    
}
