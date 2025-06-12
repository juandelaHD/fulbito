package ar.uba.fi.ingsoft1.football5.teams;

import ar.uba.fi.ingsoft1.football5.images.TeamImage;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @JsonManagedReference("team-image")
    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private TeamImage teamImage;

    private String mainColor;

    private String secondaryColor;

    private Integer ranking;

    @ManyToOne(optional = false)
    private User captain;

    @JsonManagedReference("user-teams")
    @ManyToMany
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "homeTeam")
    private Set<Match> homeMatches = new HashSet<>();

    @OneToMany(mappedBy = "awayTeam")
    private Set<Match> awayMatches = new HashSet<>();

    protected Team() {}

    public Team(String name, User captain) {
        this.name = name.toLowerCase();
        this.captain = captain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamImage getImage() {
        return teamImage;
    }

    public void setImage(TeamImage image) {
        this.teamImage = image;
    }

    public String getMainColor() {
        return mainColor;
    }

    public void setMainColor(String mainColor) {
        this.mainColor = mainColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public User getCaptain() {
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public Set<Match> getJoinedMatches() {
        Set<Match> allMatches = new HashSet<>();
        allMatches.addAll(homeMatches);
        allMatches.addAll(awayMatches);
        return allMatches;
    }
}