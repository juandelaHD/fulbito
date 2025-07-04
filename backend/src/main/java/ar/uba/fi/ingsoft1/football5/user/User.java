package ar.uba.fi.ingsoft1.football5.user;

import ar.uba.fi.ingsoft1.football5.images.AvatarImage;
import ar.uba.fi.ingsoft1.football5.images.Image;
import ar.uba.fi.ingsoft1.football5.matches.Match;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "users")
public class User implements UserDetails, UserCredentials {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String gender;

    @JsonManagedReference("user-image")
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private AvatarImage avatar;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean emailConfirmed = false;

    @Column(nullable = false)
    private boolean activeUser = true;

    @Column
    private String emailConfirmationToken;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference("organizer-match")
    private final Set<Match> organizedMatches = new HashSet<>();

    @ManyToMany(mappedBy = "players", fetch = FetchType.EAGER)
    @JsonBackReference("player-match")
    private final Set<Match> joinedMatches = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    @JsonBackReference("user-teams")
    private Set<Team> teams = new HashSet<>();

    @Column(nullable = true)
    private String invitationToken;

    protected User() {}

    public User(String username, String firstName, String lastName, String gender, String zone, Integer age, String password, Role role) {
        this.firstName = firstName.toLowerCase();
        this.lastName = lastName.toLowerCase();
        this.username = username.toLowerCase();
        this.gender = gender.toLowerCase();
        this.zone = zone.toLowerCase();
        this.age = age;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmailConfirmationToken() {
        return emailConfirmationToken;
    }

    public void setEmailConfirmationToken(String emailConfirmationToken) {
        this.emailConfirmationToken = emailConfirmationToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarImage avatar) {
        this.avatar = avatar;
    }

    public String getZone() {
        return zone;
    }

    public Integer getAge() {
        return age;
    }

    public Role getRole() {
        return role;
    }

    public Integer getMatchesPlayed() {
        return getOrganizedMatches().size();
    }

    public Set<Match> getOrganizedMatches() {
        return organizedMatches;
    }

    public Set<Match> getJoinedMatches() {
        return joinedMatches;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public boolean isActiveUser() {
        return activeUser;
    }

}
