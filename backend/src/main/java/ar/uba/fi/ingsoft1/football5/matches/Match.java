package ar.uba.fi.ingsoft1.football5.matches;

import ar.uba.fi.ingsoft1.football5.fields.Field;
import ar.uba.fi.ingsoft1.football5.matches.invitation.MatchInvitation;
import ar.uba.fi.ingsoft1.football5.teams.Team;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id",
                foreignKey = @ForeignKey(name = "fk_field_match",
                        foreignKeyDefinition = "FOREIGN KEY (field_id) REFERENCES field(id) ON DELETE SET NULL"))
    @JsonManagedReference("match-field")
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference("organizer-match")
    private User organizer;

    @ManyToMany
    @JoinTable(
            name = "match_subscriptions",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonManagedReference("player-match")
    private Set<User> players = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = true)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = true)
    private Team awayTeam;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private MatchInvitation invitation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchType type;

    @Column(nullable = false)
    private Integer minPlayers;

    @Column(nullable = false)
    private Integer maxPlayers;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean confirmationSent = false;

    // TODO: Create a result entity or enum
    @Column(nullable = false)
    private String result = "0-0";

    public Match() {}

    public Match(Field field, User organizer, MatchType type, Integer minPlayers, Integer maxPlayers, LocalDate date, LocalDateTime startTime, LocalDateTime endTime) {
        this.field = field;
        this.organizer = organizer;
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

    public MatchInvitation getInvitation() {
        return invitation;
    }

    public void setInvitation(MatchInvitation invitation) {
        this.invitation = invitation;
        if (invitation != null) {
            invitation.setMatch(this);
        }
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

    public void addHomeTeam(Team team) {
        homeTeam = team;
        homeTeam.getJoinedMatches().add(this);

        for (User player: homeTeam.getMembers()){
            this.addPlayer(player);
        }
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void addAwayTeam(Team team) {
        awayTeam = team;
        awayTeam.getJoinedMatches().add(this);

        for (User player: awayTeam.getMembers()){
            this.addPlayer(player);
        }
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void clearTeams() {
        if (homeTeam != null) {
            homeTeam.getJoinedMatches().remove(this);
            homeTeam = null;
        }
        if (awayTeam != null) {
            awayTeam.getJoinedMatches().remove(this);
            awayTeam = null;
        }
    }

    public void clearPlayers() {
        for (User player : new HashSet<>(players)) {
            removePlayer(player);
        }
    }

}
