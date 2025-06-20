package ar.uba.fi.ingsoft1.football5.images;

import ar.uba.fi.ingsoft1.football5.common.exception.ItemNotFoundException;
import ar.uba.fi.ingsoft1.football5.config.security.JwtUserDetails;
import ar.uba.fi.ingsoft1.football5.teams.Team;
import ar.uba.fi.ingsoft1.football5.teams.TeamRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TEAM")
public class TeamImage extends Image {

    @JsonBackReference("team-image")
    @OneToOne
    @JoinColumn(name = "team_id", unique = true)
    private Team team;

    @Transient
    private static TeamRepository teamRepository;

    public static void injectRepository(TeamRepository repository) {
        teamRepository = repository;
    }

    @Override
    public void validateOwnership(JwtUserDetails userDetails) throws ItemNotFoundException {
        String username = userDetails.username();
        Team currentTeam = teamRepository.findById(team.getId())
                .orElseThrow(() -> new ItemNotFoundException("team", team.getId()));

        if (!currentTeam.getCaptain().getUsername().equalsIgnoreCase(username)) {
            throw new IllegalArgumentException("You are not the captain of the team associated with this image.");
        }
    }

    protected TeamImage() {}

    public TeamImage(byte[] data, Team team) {
        super(data);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}