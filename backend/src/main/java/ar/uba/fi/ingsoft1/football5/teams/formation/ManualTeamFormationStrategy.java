package ar.uba.fi.ingsoft1.football5.teams.formation;

import ar.uba.fi.ingsoft1.football5.user.User;
import java.util.Set;
import java.util.ArrayList;

public class ManualTeamFormationStrategy implements TeamFormationStrategy {
    private final Set<User> teamAMembers;
    private final Set<User> teamBMembers;

    public ManualTeamFormationStrategy(Set<User> teamAMembers, Set<User> teamBMembers) {
        this.teamAMembers = teamAMembers;
        this.teamBMembers = teamBMembers;
    }
    @Override
    public TeamFormationResult formTeams(Set<User> players, int teamSize, Long matchId) throws IllegalArgumentException {
        if (teamAMembers.size() != teamBMembers.size()) {
            throw new IllegalArgumentException("Both teams must have the same number of members.");
        }
        Set<User> allPlayers = new java.util.HashSet<>(teamAMembers);
        allPlayers.addAll(teamBMembers);
        if (allPlayers.size() != teamAMembers.size() + teamBMembers.size()) {
            throw new IllegalArgumentException("Cannot have duplicate players in teams.");
        }
        return TeamFormationUtils.buildTeams(
                new ArrayList<>(teamAMembers),
                new ArrayList<>(teamBMembers),
                matchId
        );
    }
}